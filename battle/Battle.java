package battle;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import monster.Monster;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Solution to JP2 lab 8 2019. Represents Battles between monsters in a battling
 * game.
 * 
 * @author 2327851
 * 
 *         Note: Code from @author mefoster's tutorial on 17th of November has
 *         been used as an example and inspiration for the solution of this task
 *
 */
public class Battle {
	/** All the variables that will help us manage how the threads are handled */

	protected ReentrantLock myLock = new ReentrantLock();
	protected Condition myCon = myLock.newCondition();
	protected AtomicReference<Monster> myRef = new AtomicReference();

	public static void main(String[] args) {
		Battle fight = new Battle();

		/** Generates a list of three monsters */
		List<Monster> monsters = Utils.generateMonsters(3);
		/** Generates a list where all the different threads will be stored */
		List<Thread> threads = new ArrayList<>();

		/**
		 * Creates a separate thread for each monster in our list of monsters. Each
		 * thread handles a battle.
		 */
		for (Monster m : monsters) {
			threads.add(new Thread(new MonsterRunner(m, fight)));
			System.out.println("Created thread");
		}

		/** Start the threads */
		for (Thread t : threads) {
			t.start();
			System.out.println("Started thread");
		}
		/**
		 * Make the threads wait for 10 seconds. On a failure, state the interuption
		 * happened during the sleeping process
		 */
		try {
			Thread.sleep(10000);
			System.out.println("Waiting...");
		} catch (InterruptedException e) {
			System.out.println(Thread.currentThread().getId() + ": interrupted in sleep()");
		}

		/** Interrupt all the threads */
		for (Thread t : threads) {
			t.interrupt();
			System.out.println("Successfully Interrupted Thread");
		}

		/**
		 * Attempt to join the threads, on failure state that interruption happened
		 * during joining process
		 */
		for (Thread t : threads) {
			try {
				t.join();
				System.out.println("Joining...");
			} catch (InterruptedException e) {
				System.out.println(Thread.currentThread().getId() + ": interrupted in join()");
			}
		}

	}
}
