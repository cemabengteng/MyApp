package com.example.aaa;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import xiaofei.library.shelly.Shelly;
import xiaofei.library.shelly.function.Action1;
import xiaofei.library.shelly.function.Function1;

import static junit.framework.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testToyRoom(){
        Shelly.<String>createDomino("file name")
                .background()
                .flatMap(new Function1<String, List<Byte>>() {
                    @Override
                    public List<Byte> call(String input) {
//                        File[] files = new File(input).listFiles();
                        byte[] bytes = input.getBytes();
                        List<Byte> result = new ArrayList<>();
                        for(Byte by : bytes){
                            result.add(by);
                        }
                        return result;
                    }
                })
                .perform(new Action1<Byte>() {
                    @Override
                    public void call(Byte input) {
                        System.out.print(input.toString());
                    }
                })
                .commit();
    }

    @Test
    public void test(){
        long playDuration = 0;
        System.out.print(playDuration / 3600);
        System.out.print(playDuration % 3600 / 60);
        System.out.print(playDuration % 60);
    }
}