package battle;

import java.util.concurrent.atomic.AtomicReference;

import monster.Monster;

public class MonsterRunner implements Runnable {
	protected Monster m1;
	protected Battle fight;

	public MonsterRunner(Monster m1, Battle fight) {
		this.m1 = m1;
		this.fight = fight;
	}

	/**
	 * Executes the actual battle if possible. On failure, states in which process
	 * the interruption occurred.
	 */
	public void run() {
		// as long as the monster isn't knocked out
		while (m1.getHitPoints() > 0) {
			try {
				// attempt to get the battle lock
				fight.myLock.lockInterruptibly();
			} catch (InterruptedException e) {
				System.out.println(Thread.currentThread().getId() + ": interrupted in lock()");
				break;
			}

			// if no waiting monster is given, set the waiting monster to the given monster
			if (fight.myRef.compareAndSet(null, m1)) {
				try {
					fight.myCon.await();
				} catch (InterruptedException e) {
					fight.myLock.unlock();
					System.out.println(Thread.currentThread().getId() + ": interrupted in await()");
					break;
				}
				// otherwise start the battle with the waiting monster and then set the field to
				// zero
			} else {
				Monster m2 = fight.myRef.getAndSet(null);
				// determine whose turn to attack it is and execute the attack accordingly
				if (Utils.RAND.nextBoolean()) {
					Utils.doAttack(m1, m2);
				} else {
					Utils.doAttack(m2, m1);
				}
				fight.myCon.signal();
			}
			// Release the lock
			fight.myLock.unlock();
			System.out.println(Thread.currentThread().getId() + ": released the lock");

			// After executing the method, wait for 5 seconds
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				System.out.println(Thread.currentThread().getId() + ": interrupted in sleep()");
				break;
			}
		}
	}
}
