import java.util.concurrent.CyclicBarrier;
import java.util.function.Consumer;

public class Car  implements Runnable{
    private static int CARS_COUNT;
    private Race race;
    private int speed;
    private String name;
    private final CyclicBarrier prepareCdl;
    private final Consumer<Car> finishCallBack;

    public String getName() {return  name;}
    public int getSpeed() {return speed;}
    public Car (Race race, int speed, CyclicBarrier prepareCdl, Consumer<Car> finishCallBack) {
        this.race =  race;
        this.speed = speed;
        CARS_COUNT++;
        this.name = "Участник #" + CARS_COUNT;

        this.prepareCdl = prepareCdl;
        this.finishCallBack = finishCallBack;

    }

    @Override
    public void run() {
        try {
            System.out.println(this.name + "готовиться");
            Thread.sleep(500 + (int)(Math.random()*800));
            System.out.println(this.name + "готов");

            prepareCdl.await();
        }catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < race.getStages().size(); i++) {
            race.getStages().get(i).go(this);
        }
        finishCallBack.accept(this);

    }
}
