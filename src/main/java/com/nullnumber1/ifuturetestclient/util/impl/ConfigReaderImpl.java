package com.nullnumber1.ifuturetestclient.util.impl;

import com.nullnumber1.ifuturetestclient.util.ConfigReader;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ConfigReaderImpl implements ConfigReader {

    @Override
    @SneakyThrows
    public Map<String, Integer> readClients() {
        File file = ResourceUtils.getFile("classpath:config");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        return reader.lines().collect(Collectors.toMap(line -> line.split(":")[0], line -> Integer.parseInt(line.split(":")[1])));
    }
}
