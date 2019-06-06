package async.example;

import async.DefaultPromise;
import async.Future;
import async.Promise;

/**
 * @author leexuehan on 2019/6/5.
 */

//Future and Promise 的一些例子
public class Demo {
    public static void main(String[] args) throws InterruptedException {
        new Demo().testFuture();
    }

    private void testFuture() throws InterruptedException {
        Cooker cooker = new Cooker();
        Future future = cooker.cookFood();
        System.out.println("waiting for cook done...");
        future.await();
        System.out.println("cook finished");

    }

    class Cooker {
        Food food;

        public Future cookFood() {
            Promise<Food> result = new DefaultPromise<>();
            new Thread(new Worker(result)).start();
            return result;
        }

        class Worker implements Runnable {
            private Promise<Food> foodPromise;

            Worker(Promise<Food> promise) {
                this.foodPromise = promise;
            }

            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                food = new Food("noodles");
                food.type = "dinner";
                this.foodPromise.setSuccess(food);
            }
        }
    }

    class Food {
        String name;
        String type;

        Food(String name) {
            this.name = name;
        }
    }
}
