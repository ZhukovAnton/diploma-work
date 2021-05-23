package com.stanum.skrudzh.utils;

import com.stanum.skrudzh.saltage.SaltedgeAPI;
import com.stanum.skrudzh.saltage.model.Customer;
import com.stanum.skrudzh.saltage.model.Deleted;
import com.stanum.skrudzh.saltage.model.Response;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
public class DestroyCustomers {

    @Autowired
    private SaltedgeAPI saltedgeAPI;

    @Test
    @Disabled
    public void destroyCustomers() throws Exception {
//        List<Customer> customers = saltedgeAPI.custom.findAllCustomers();
//
//        ExecutorService executorService = Executors.newFixedThreadPool(200);
//        int count = 0;
//        for(Customer customer : customers) {
//            count++;
//
//            executorService.execute(new Runnable() {
//                @Override
//                public void run() {
//                    Response<Deleted> remove = saltedgeAPI.customer.remove(customer.getCustomerId());
//                }
//            });
//        }
//
//        executorService.awaitTermination(10000, TimeUnit.MINUTES);
    }
}
