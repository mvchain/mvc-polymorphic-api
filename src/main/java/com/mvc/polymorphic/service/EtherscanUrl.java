package com.mvc.polymorphic.service;

public interface EtherscanUrl {

    public final static String txlist = "?module=account&action=txlist&startblock=0&endblock=99999999&sort=asc&address=%s";
}
