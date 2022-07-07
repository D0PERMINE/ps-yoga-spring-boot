package com.example.demo.service;

import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ReadWriteManagerTest {
    private final String fileName = "test3.txt";

    @Test
    void testWriteAndRead(){
        String testAccesToken= "abcdefg333";
        String testRefreshToken= "asdfghj333";
        ReadWriteInTextFileManager.writeIntoFile(testAccesToken,testRefreshToken, fileName);
        String tokenStrings = ReadWriteInTextFileManager.readFromFile(fileName);
        System.out.println(tokenStrings);
        Assertions.assertEquals(testAccesToken + "\n" + testRefreshToken, tokenStrings);
    }
}
