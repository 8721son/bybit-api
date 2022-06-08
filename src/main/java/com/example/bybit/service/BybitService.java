package com.example.bybit.service;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

public class BybitService {
    @Value("${bybit.public-key}")
    private String public_key;

    @Value("${bybit.private-key}")
    private String private_key;

    public void getBalance() throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        var map = new TreeMap<String, String>(
                new Comparator<String>() {
                    public int compare(String obj1, String obj2) {
                        //sort in alphabet order
                        return obj1.compareTo(obj2);
                    }
                });

        map.put("coin", "BTC");
        map.put("timestamp", ZonedDateTime.now().toInstant().toEpochMilli()+"");

        map.put("api_key", public_key);

        String queryString = genQueryString(map, private_key);

        OkHttpClient client = new OkHttpClient();
        RequestBody body=RequestBody.create(new byte[0]);
        Request request = new Request.Builder()
                .get()
                .url("https://api.bybit.com/v2/private/wallet/balance?"+queryString)
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            System.out.println(response.body().string());
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public String genQueryString(TreeMap<String, String> params, String secret) throws NoSuchAlgorithmException, InvalidKeyException {
        Set<String> keySet = params.keySet();
        Iterator<String> iter = keySet.iterator();
        StringBuilder sb = new StringBuilder();
        while (iter.hasNext()) {
            String key = iter.next();
            sb.append(key + "=" + params.get(key));
            sb.append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        return sb+"&sign="+bytesToHex(sha256_HMAC.doFinal(sb.toString().getBytes()));
    }

    public String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
