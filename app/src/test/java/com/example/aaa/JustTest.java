package com.example.aaa;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by chengXing on 2016/10/26.
 */
public class JustTest {
    @Test
    public void testBehave() {
        List mockList = mock(List.class);
        mockList.add("one");
        mockList.clear();

        verify(mockList).add("one");
        verify(mockList).clear();
    }

    @Test
    public void testStub() {
        ArrayList mockArrayList = mock(ArrayList.class);
        when(mockArrayList.get(0)).thenReturn("first");
        when(mockArrayList.get(1)).thenReturn(new RuntimeException());

        System.out.print(mockArrayList.get(1));

        verify(mockArrayList).get(1);
    }

    @Mock
    ArrayList mockArraylist;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    private ArgumentMatcher<Integer> isValid() {
        return new ArgumentMatcher<Integer>() {
            @Override
            public boolean matches(Integer argument) {
                if (argument < 10){
                    return true;
                }else {
                    return false;
                }
            }
        };
    }

    @Test
    public void testMatchers() {
        when(mockArraylist.get(anyInt())).thenReturn("anyInt");
//        when(mockArraylist.get(argThat(isValid()))).thenReturn("argThat");


        System.out.print(mockArraylist.get(999));
        verify(mockArraylist).get(anyInt());
    }


}
