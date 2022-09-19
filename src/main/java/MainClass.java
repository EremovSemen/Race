import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

public class MainClass {
    public static final int CARS_COUNT = 4;

    private static final CyclicBarrier prepareBarrier = new CyclicBarrier(CARS_COUNT + 1);
    private static final Semaphore tunnelSemaphore = new Semaphore(CARS_COUNT / 2);

    private static volatile boolean isFinishedDetected = false;
    private static Consumer<Car> finishCollback = (car) -> {
        synchronized (MainClass.class) {
            if (!isFinishedDetected) {
                System.out.println(car.getName() + " - WIN");
                isFinishedDetected = true;
            }
        }
    };


    public static void main(String[] args) {
        System.out.println("ВАЖНОЕ ОБЬЯВЛЕНИЕ >>> Подготовка!!!");

        Race race = new Race(
                new Road(60),
                new Tunnel(tunnelSemaphore),
                new Road(40)
        );
        Car[] cars = new Car[CARS_COUNT];
        for (int i = 0; i < cars.length;i++){
            cars[i] = new Car(
                    race, 20 + (int) (Math.random() * 10),
                    prepareBarrier,
                    finishCollback
            );
        }
//            for (int i = 0; i < cars.length; i++) {
//                new Thread(cars[i]).start();
//            }

            List<Thread> threads = Arrays.stream(cars)
                    .map(Thread::new)
                    .peek(Thread::start).toList();

            try {
                prepareBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }

            System.out.println("ВАЖНОЕ ОБЪЯСНЕНИЕ >>> Гонка началась!!!");

            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            System.out.println("ВАЖНОЕ ОБЬЯВЛЕНИЕ >>> Гонка закончлась!!!");
        }
    }


