package bgu.spl.mics;

import java.util.concurrent.TimeUnit;


/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 *
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> {

	private T result;
	private boolean resolve;


	/**
	 * This should be the only public constructor in this class.
	 */
	public Future() {
		result = null;
		resolve = false;
	}

	/**
	 * retrieves the result the Future object holds if it has been resolved.
	 * This is a blocking method! It waits for the computation in case it has
	 * not been completed.
	 * <p>
	 * @return return the result of type T if it is available, if not wait until it is available.
	 *
	 */
	/**
	 * @pre none
	 * @post resolve==true && result!=null
	 */
	public T get(){
		synchronized(this){
			while (!isDone()) {
				try {
					this.wait();
				}
				catch (InterruptedException e){
					System.out.println(e.getMessage());
				}
			}
		}
		return result;
	}

	/**
	 * Resolves the result of this Future object.
	 */

	/**
	 * @param result - result of the operation
	 * @pre result!=null && resolve==false && this.result==null
	 * @post this.result!=null && resolve==true
	 */
	public void resolve (T result) {
		synchronized (this){
			this.result = result;
			resolve = true;
			this.notifyAll();
		}
	}

	/**
	 * @return true if this object has been resolved, false otherwise
	 */

	public boolean isDone() {
		synchronized (this) {
			return resolve;
		}
	}

	/**
	 * retrieves the result the Future object holds if it has been resolved,
	 * This method is non-blocking, it has a limited amount of time determined
	 * by {@code timeout}
	 * <p>
	 * @param timout 	the maximal amount of time units to wait for the result.
	 * @param unit		the {@link TimeUnit} time units to wait.
	 * @return return the result of type T if it is available, if not,
	 * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
	 *         elapsed, return null.
	 */

	/**
	 * @pre none
	 * @post trivial
	 */
	public T get(long timeout, TimeUnit unit) {
		synchronized (this){
			if (resolve) {
				return result;
			}
			try {
				this.wait(unit.toMillis(timeout));
				if (resolve)
					return result;
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
		}
		return null;
	}
}