package com.disaster.chatgpt.adapter;


import org.junit.Assert;
import org.junit.Test;

import java.io.File;


public class WechatAdapterTest {

    @Test
    public void filePathTest() {
        File file = new File(System.getProperty("user.dir")+WechatAdapter.picPrefix + File.separator + "极简.jpeg");
        Assert.assertTrue(file.exists());
    }
}
