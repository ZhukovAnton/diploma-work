package com.stanum.skrudzh.utils;

import com.stanum.skrudzh.saltage.model.Transaction;

import java.util.List;

public class LoggerUtil {

    public static String printTrs(List<Transaction> trs) {
        String result = "";
        int i = 0;
        for(Transaction tr : trs) {
            if(i >= 5) {
                break;
            }
            result+=printTr(tr);
            i++;
        }
        return  "[" + result + "]";
    }

    public static String printTr(Transaction tr) {
        return "{ id=" + tr.getId() + ",accountId=" + tr.getAccountId()+ ", category=" + tr.getCategory() +
                ", description=" + tr.getDescription() + ", amount=" + tr.getAmount() + "}";
    }
}
