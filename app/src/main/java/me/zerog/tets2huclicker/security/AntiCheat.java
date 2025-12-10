package me.zerog.tets2huclicker.security;


import java.util.Stack;
import java.util.concurrent.atomic.AtomicLong;

public class AntiCheat {

    private static final int CTS_AMOUNT = 10;
    private static final Stack<Long> click_time_stamps = new Stack<>();

    //Deltas to info. How many deltas should be measured before we make our final judgement
    private static final int DTI_AMOUNT = 30;
    //Long-run autoclicker detection. Human can't click with the exact same speed for too long.
    //TODO Adjust the data
    private static final int MIN_TIME_BETWEEN_DELTAS_MS = 80;
    private static final Stack<Long> deltas = new Stack<>();

    //TODO Самая большая проблема будет в том, что компьютер может предоставлять поддельные данные времени для анти-чита. Чтобы противостоять этому, нужно синхронизировать время с сервером и проверять разницу во времени кликов на подлинность

    public static synchronized void addStamp(){
        if(click_time_stamps.size() <= CTS_AMOUNT){
            click_time_stamps.push(System.currentTimeMillis());
            //System.out.println("Pushed stamp! Size - "+click_time_stamps.size());
        }else {
            //System.out.println("Flushing!");
            flushTimeStamps();
        }
    }

    private static synchronized void flushTimeStamps(){
        AtomicLong time_delta_sum = new AtomicLong(0);
        AtomicLong last_stamp = new AtomicLong(System.currentTimeMillis());
        Stack<Long> stamps = (Stack<Long>) click_time_stamps.clone();
        click_time_stamps.clear();

        long stamp;
        while(true) {
            try {
                stamp = stamps.pop();
            }catch (Exception ignored){break;}
            time_delta_sum.getAndAdd(last_stamp.get() - stamp);
            last_stamp.set(stamp);
        }

        //TODO Mark as cheating -> consequences (If delta <= 32)

        //Add delta to deltas
        if(deltas.size() <= DTI_AMOUNT){
            deltas.push(time_delta_sum.get() / CTS_AMOUNT);
        }else{
            flushDeltas();
        }
    }

    private static synchronized void flushDeltas(){
        AtomicLong deltas_sum = new AtomicLong(0);
        deltas.stream().iterator().forEachRemaining(deltas_sum::getAndAdd);
        deltas.clear();
        deltas_sum.set(deltas_sum.get() / DTI_AMOUNT);

        if(deltas_sum.get() < MIN_TIME_BETWEEN_DELTAS_MS){
            //TODO Mark as cheating -> consequences
        }
    }
}
