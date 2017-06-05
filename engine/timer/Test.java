package de.jroeger.engine.timer;

public class Test {

	public static void main(String[] args) {
		Timer t = Timer.getInstance();
		Obja o = new Obja();
		
		t.addEvent(o, "twst", 2000);
		t.addEvent(o, "twst1", 5000);
		t.addEvent(o, "twst2", 3000);
		t.addEvent(o, "twst3", 7000);
		t.addEvent(o, "twst4", 1000);
	}

	private static class Obja implements Timeable {

		public Obja() {
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void timedEventFinished(Object param) {
			String str = (String) param;
			
			System.out.println(str);
		}
		
	}
}
