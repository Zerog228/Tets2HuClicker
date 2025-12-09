package me.zerog.tets2huclicker.security;


import java.util.Stack;
import java.util.concurrent.atomic.AtomicLong;

public class AntiCheat {

    private static final Stack<Long> click_time_stamps = new Stack<>();

    //TODO А если на компе остановлено время? Тогда дельта = 0 и всё в порядке.
    //TODO Самая большая проблема будет в том, что компьютер может предоставлять поддельные данные времени для анти-чита. Чтобы противостоять этому, нужно синхронизировать время с сервером и проверять разницу во времени кликов на подлинность

    //TODO Хранить несколько дельт. Если они всё время одинаковые то, вероятно, игрок читерит. То же самое для отдельных меток внутри каждой дельты

    public static void addStamp(){
        if(click_time_stamps.size() < 10){
            click_time_stamps.push(System.currentTimeMillis());
            //System.out.println("Pushed stamp! Size - "+click_time_stamps.size());
        }else {
            //System.out.println("Flushing!");
            flush();
        }
    }

    private static void flush(){
        AtomicLong time_delta = new AtomicLong(0);
        AtomicLong last_stamp = new AtomicLong(System.currentTimeMillis());
        click_time_stamps.iterator().forEachRemaining(stamp -> {
            //System.out.println("Time stamp - "+stamp);
            time_delta.getAndAdd(last_stamp.get() - stamp);
            last_stamp.set(stamp);
            //System.out.println("New delta - "+time_delta.get());
        });
        click_time_stamps.clear();
        //System.out.println("Delta - "+time_delta.get()); //If delta <=32 -> cheating
    }
}
