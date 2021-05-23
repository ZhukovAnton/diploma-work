package com.stanum.skrudzh.saltage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.stanum.skrudzh.controller.form.saltedge.InteractiveParams;
import com.stanum.skrudzh.metrics.Metric;
import com.stanum.skrudzh.metrics.MetricType;
import com.stanum.skrudzh.metrics.MetricsService;
import com.stanum.skrudzh.saltage.model.*;
import com.stanum.skrudzh.service.saltedge.connection.data.InteractiveBuilder;
import com.stanum.skrudzh.service.saltedge.learn.data.LearnData;
import com.stanum.skrudzh.service.saltedge.learn.data.LearnRequest;
import com.stanum.skrudzh.service.saltedge.learn.data.LearnResponse;
import com.stanum.skrudzh.service.saltedge.learn.data.LearnSaltTr;
import com.stanum.skrudzh.utils.LoggerUtil;
import com.stanum.skrudzh.utils.provider.Provider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SignatureException;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SaltedgeAPI {
    private static final String API_PATH = "https://www.saltedge.com/api/v5/";
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final int EXPIRATION_TIME = 3;

    static {
        JSON.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public SessionAPI session = new SessionAPI();
    public CustomAPI custom = new CustomAPI();
    public CustomerAPI customer = new CustomerAPI();
    public AccountAPI account = new AccountAPI();
    public ConnectionAPI connection = new ConnectionAPI();
    public TransactionAPI transaction = new TransactionAPI();
    public LearnAPI learn = new LearnAPI();
    public ProviderAPI provider =  new ProviderAPI();

    @Value("${threebaskets.saltedge.app.id}")
    private String appId;
    @Value("${threebaskets.saltedge.secret}")
    private String secret;
    @Value("${threebaskets.saltedge.sign-enabled}")
    private boolean signEnabled;

    private final SignService signService;

    private final MetricsService metricsService;

    public <T> Response<T> doCall(HttpRequest req, String jsonParams, Class<T> clazz) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, EXPIRATION_TIME);
            long expiresAt = calendar.getTimeInMillis() / 1000;

            req = req
                    .header("Accept", "application/json")
                    .header("content-type", "application/json")
                    .header("App-id", appId)
                    .header("Secret", secret)
                    .header("Expires-at", String.valueOf(expiresAt));
            if(signEnabled) {
                String signatureString = buildSignatureString(req, expiresAt, jsonParams);
                String signature = signatureString;
                try {
                    signature = signService.sign(signatureString.getBytes());
                } catch (SignatureException e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                }
                req = req.header("Signature", signature);
            }

            HttpResponse<JsonNode> resp;
            if (jsonParams != null && !jsonParams.isEmpty() && req instanceof HttpRequestWithBody) {
                resp = ((HttpRequestWithBody) req).body(jsonParams).asJson();
            } else {
                resp = req.asJson();
            }

            JSONObject json = resp
                    .getBody()
                    .getObject();

//            log.info("JSON: {}", json);

            Response<T> response = parse(json, clazz);
            if(response.hasError()) {
                log.error("Error while processing SaltEdge request: {}", response.getError());
            }
            return response;
        } catch (UnirestException | JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private <T> Response<T> parse(JSONObject json, Class<T> clazz) throws JsonProcessingException {
        if (json.has("error")) {
            return new Response<>(JSON.readValue(json.getJSONObject("error").toString(), Response.Error.class));
        }
        if (json.has("data")) {
            JSONObject dataObject = json.optJSONObject("data");
            if (dataObject != null) {
                T val = JSON.readValue(dataObject.toString(), clazz);
                return new Response<>(val);
            } else {
                JSONArray array = json.optJSONArray("data");
                JavaType type = JSON.getTypeFactory().constructCollectionType(List.class, clazz);
                List<T> val = JSON.readValue(array.toString(), type);
                Response.Meta meta = null;
                if (json.has("meta")) {
                    meta = JSON.readValue(json.getJSONObject("meta").toString(), Response.Meta.class);
                }
                return new Response<>(val, meta);
            }
        } else {
            return new Response<>();
        }
    }

    private <T> Response<T> doPost(String url, String jsonParams, Class<T> clazz) {
        return doCall(Unirest.post(url), jsonParams, clazz);
    }

    private <T> Response<T> doPut(String url, String jsonParams, Class<T> clazz) {
        return doCall(Unirest.put(url), jsonParams, clazz);
    }

    private <T> Response<T> doPut(String url, Class<T> clazz) {
        return doPut(url, null, clazz);
    }

    private <T> Response<T> doGet(String url, Class<T> clazz) {
        return doCall(Unirest.get(url), null, clazz);
    }

    private <T> Response<T> doDelete(String url, Class<T> clazz) {
        return doCall(Unirest.delete(url), null, clazz);
    }

    private <T> Response<T> getAllPagedData(Function<String, Response<T>> f) {
        Response<T> resp = f.apply(null);
        while (resp.hasNext()) {
            Response<T> next = f.apply(resp.getMeta().getNextId());
            resp.merge(next);
        }
        return resp;
    }

    private String buildSignatureString(HttpRequest req, long expiresAt, String body) {
        String signatureString = String.format("%d|%s|%s|", expiresAt, req.getHttpMethod().toString(), req.getUrl());
        if (body != null && !body.isBlank()) {
            signatureString = signatureString + body;
        }
        return signatureString;
    }

    public class CustomerAPI {

        public Response<Customer> list() {
            return doGet(API_PATH + "customers", Customer.class);
        }

        public Response<Customer> list(String fromId) {
            return doGet(API_PATH + "customers?from_id=" + fromId, Customer.class);
        }

        public Response<Customer> create(String email) {
            if (email == null || email.isEmpty()) {
                throw new RuntimeException("email is empty");
            }
            long start = System.currentTimeMillis();
            Response<Customer> customerResponse = doPost(API_PATH + "customers", "{\"data\": { \"identifier\": \"" + email + "\"}}", Customer.class);
            metricsService.saveMetric(MetricType.CREATE_SALTEDGE_CUSTOMER_CALL, start);
            return customerResponse;
        }

        public Response<Customer> get(String customerId) {
            return doGet(API_PATH + "customers/" + customerId, Customer.class);
        }

        public Response<Deleted> remove(String customerId) {
            return doDelete(API_PATH + "customers/" + customerId, Deleted.class);
        }

        public Response<Locked> lock(String customerId) {
            return doPut(API_PATH + "customers/" + customerId + "/lock", Locked.class);
        }

        public Response<Locked> unlock(String customerId) {
            return doPut(API_PATH + "customers/" + customerId + "/unlock", Locked.class);
        }
    }

    public class TransactionAPI {

        public Response<Transaction> list(String connectionId, String accountId, String fromId) {
            return doGet(API_PATH + "transactions?connection_id=" + connectionId + "&account_id=" + accountId + "&from_id=" + fromId, Transaction.class);
        }

        public Response<Transaction> list(String connectionId) {
            return doGet(API_PATH + "transactions?connection_id=" + connectionId, Transaction.class);
        }

        public Response<Transaction> list(String connectionId, String accountId) {
            return doGet(API_PATH + "transactions?connection_id=" + connectionId + "&account_id=" + accountId, Transaction.class);
        }

        public Response<Transaction> pending(String connectionId, String accountId, String fromId) {
            return doGet(API_PATH + "transactions/pending?connection_id=" + connectionId + "&account_id=" + accountId + "&from_id=" + fromId, Transaction.class);
        }

        public void duplicate(String customerId, List<String> transactionIds) {
            String transactionIdsThroughComma = transactionIds
                    .stream()
                    .map(transactionId -> "\"" + transactionId + "\"")
                    .collect(Collectors.joining(", "));
            String jsonParams = "{\n" +
                    "\"data\": {\n" +
                    "\"customer_id\": \"" + customerId + "\",\n" +
                    "\"transaction_ids\": [" + transactionIdsThroughComma + "]\n" +
                    "}\n" +
                    "}";

            doPut(API_PATH + "transactions/duplicate", jsonParams, Duplicate.class);
        }

    }

    public class LearnAPI {
        public Response<LearnResponse> learn(String customerId, String transactionId, String categoryCode) {
            log.info("[Learn SaltEdge] Send API request, customerId={}, transactionId={}, categoryCode={}", customerId, transactionId, categoryCode);

            LearnRequest req = new LearnRequest();
            LearnData data = new LearnData();
            data.setCustomerId(customerId);
            data.setTransactions(Collections.singletonList(new LearnSaltTr(transactionId, categoryCode, true)));
            req.setData(data);

            Response<LearnResponse> response;
            try {
                response = doPost(API_PATH + "/categories/learn", JSON.writeValueAsString(req), LearnResponse.class);
            } catch (JsonProcessingException e) {
                log.error("Error while execute Learn API", e);
                throw new RuntimeException(e);
            }
            log.info("Learn Saltedge response: {}", response.getData());
            return response;
        }
    }

    public class AccountAPI {
        public Response<Account> list(String connectionId, String fromId) {
            return doGet(API_PATH + "accounts?connection_id=" + connectionId + "&from_id=" + fromId, Account.class);
        }

        public Response<Account> list(String connectionId) {
            return doGet(API_PATH + "accounts?connection_id=" + connectionId, Account.class);
        }
    }

    public class ConnectionAPI {

        public Response<Connection> list(String customerId, String fromId) {
            return doGet(API_PATH + "connections?customer_id=" + customerId + "&from_id=" + fromId, Connection.class);
        }

        public Response<Connection> list(String customerId) {
            return doGet(API_PATH + "connections?customer_id=" + customerId, Connection.class);
        }

        public Response<Connection> show(String connectionId) {
            return doGet(API_PATH + "connections/" + connectionId, Connection.class);
        }

        public Response<Connection> interactive(String connectionId, List<InteractiveParams> params) {
            String jsonParams = InteractiveBuilder.buildReq(params);
            log.info("Send interactive credentials by SaltEdgeId={}: {}", connectionId, jsonParams);
            return doPut(API_PATH + "connections/" + connectionId + "/interactive", jsonParams, Connection.class);
        }

        public void destroy(String connectionId) {
            doDelete(API_PATH + "connections/" + connectionId, Removed.class);
        }

        public void refresh(String connectionId) {
            String jsonParams = "{\n" +
                    "\"data\": {\n" +
                    "\"consent\": {\n" +
                    "\"scopes\": [\n" +
                    "\"account_details\",\n" +
                    "\"transaction_details\"\n" +
                    "]\n" +
                    "}\n" +
                    "}\n" +
                    "}";
            doPut(API_PATH + "connections/" + connectionId + "/refresh", jsonParams, Connection.class);
        }
    }

    public class SessionAPI {

        public Response<SaltEdgeSession> create(String customerId) {
            String request = "{" +
                    "\"data\": { " +
                    "\"customer_id\": \"" + customerId + "\", " +
                    "\"consent\": { " +
                    "\"scopes\": [ " +
                    "\"account_details\" " +
                    "] " +
                    "}, " +
                    "\"attempt\": { " +
                    "\"from_date\": \"2019-02-01\", " +
                    "\"fetch_scopes\": [ " +
                    "\"accounts\"" +
                    "] " +
                    "} " +
                    "} " +
                    "}";
            return doPost(API_PATH + "connect_sessions/create", request, SaltEdgeSession.class);

        }

    }

    public class ProviderAPI {
        public Response<Provider> list(String from) {
            return doGet(API_PATH + "providers?from_id=" + from, Provider.class);
        }
    }

    public class CustomAPI {

        public List<Customer> findAllCustomers() {
            long start = System.currentTimeMillis();
            Response<Customer> customers = getAllPagedData(next -> customer.list(next));
            metricsService.saveMetric(MetricType.FIND_ALL_CUSTOMERS, start);
            log.info("[Find all customers] Return {} customers", customers.getData().size());
            return customers.getData();
        }

        public List<Account> findAllAccountsWithTransactions(String connectionId) {
            Response<Account> accounts = getAllPagedData(next -> account.list(connectionId, next));
            accounts.getData()
                    .forEach(account ->
                            account.setTransactions(findAllTransactions(connectionId, account.getId())));
            List<Account> data = accounts.getData();
            log.info("[Find accounts by connectionId={}] Return {} accounts", connectionId, data.size());
            return data;
        }

        public List<Connection> findAllConnectionsByProviderId(String customerId, String providerId) {
            Response<Connection> connections = getAllPagedData(next -> connection.list(customerId, next));
            return connections.getData()
                    .stream()
                    .filter(connection -> connection.getProviderId().equals(providerId))
                    .collect(Collectors.toList());
        }

        public List<Account> findAllAccounts(String connectionId) {
            Response<Account> accounts = getAllPagedData(next -> account.list(connectionId, next));
            log.info("[Find all accounts] Return {} accounts for connectionId={}", accounts.getData().size(), connectionId);
            return accounts.getData();
        }

        public List<Transaction> findAllTransactions(String connectionId, String accountId) {
            Response<Transaction> postedTransactions = getAllPagedData(next -> transaction.list(connectionId, accountId, next));
            Response<Transaction> pendingTransactions = getAllPagedData(next -> transaction.pending(connectionId, accountId, next));
            List<Transaction> allTransactions = postedTransactions.getData();
            allTransactions.addAll(pendingTransactions.getData());
            log.info("[Find all transactions] return {} transactions for connectionId={}, accountId={},  {}",
                    allTransactions.size(), connectionId, accountId, LoggerUtil.printTrs(allTransactions));
            return allTransactions;
        }

        public List<Provider> findAllProviders() {
            Response<Provider> customers = getAllPagedData(s -> provider.list(s));
            log.info("[Find all providers] Return {} customers", customers.getData().size());
            return customers.getData();
        }
    }


}