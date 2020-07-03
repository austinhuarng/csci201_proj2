package models;

public class Orders extends Thread{
	private Schedule sch;
   
	public Orders(Schedule sch){
		this.sch = sch;
	}

	public void run(){
		try {
			Thread.sleep((long) (sch.getTime()*1000));
			double sleeptime = 2*sch.restaurant.getDistance();	
			util.Util.printMessage("Starting delivery of " + sch.getItem() + " from " + sch.restaurant.getName() + "!");
			try{
				sch.restaurant.semaphore.acquire();
				Thread.sleep((long)sleeptime*1000);
			}catch(InterruptedException ie){
				System.out.println("ie delivering");
			}finally{
				sch.restaurant.semaphore.release();
				util.Util.printMessage("Finishing delivery of " + sch.getItem() + " from " + sch.restaurant.getName() + "!");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
}
