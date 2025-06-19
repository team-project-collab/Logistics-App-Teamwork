package com.company.oop.logistics.utils.misc;

import java.time.LocalDateTime;

public class ComparingHelpers {
    public static boolean doTimeFramesOverlap(LocalDateTime startTime1, LocalDateTime endTime1,
                                           LocalDateTime startTime2, LocalDateTime endTime2){
        return !endTime1.isBefore(startTime2) && !endTime2.isBefore(startTime1);
    }
}
