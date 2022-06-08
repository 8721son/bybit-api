package com.example.bybit.controller;

import com.example.bybit.service.BybitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/bybit")
@RequiredArgsConstructor
public class BybitController {

    private final BybitService bybitService;

    @GetMapping("/")
    public void test() throws NoSuchAlgorithmException, IOException, InvalidKeyException {
        bybitService.getBalance();
    }

}
