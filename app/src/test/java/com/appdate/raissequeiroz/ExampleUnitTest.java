package com.appdate.agendamento;

import android.content.Context;

import com.appdate.agendamento.util.DateUtils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void test() {
        System.out.println(DateUtils.timeMilisToString(-61530632993451L));
    }


}