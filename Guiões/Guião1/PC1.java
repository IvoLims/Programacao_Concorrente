/* 1. Escreva um programa que crie N threads, em que cada uma imprime os números de 1 a I, e depois espere que estas terminem. */

class MyThread extends Thread{
	public int I;
	public void run(){
		for(int i = 0; i<I; i++){
			System.out.println(i+1);
		}
	}
}

public class Ex1{
	public static void main(String[] args){
		try{
			final int N = Integer.parseInt(args[0]);
		    final int I = Integer.parseInt(args[1]);
		    for(int i = 0; i<N; i++){
			    Thread t = new MyThread();
			    t.I = I;
			    t.start();
			    t.join();
		}
		System.out.println("Finish");
	} catch (InterruptedException e){
		e.printStackTrace();
	}
}

//--------------------------Outra Forma Ex1-----------------------------------------//

class Incrementer extends Thread{
	final int iterations;
	Printer(int iterations){ this.iterations = iterations;}
	public void run(){
		for(int i = 0; i<iterations; ++i){
			System.out.println(i+1);
		}
	}
}

class Main{
	public static void main(String[] args) throws InterruptedException{
		final int N = 	Integer.parseInt(args[0]);
		final int I = 	Integer.parseInt(args[1]);
		for(int i = 0; i<N; ++i){
			new Printer(I).start();
		}
	}
}

/* 2. Modifique o programa para as N threads terem acesso a um único objecto partilhado, de uma classe Counter. Cada thread 
deverá agora, em vez de imprimir números, incrementar I vezes o contador. Escreva duas versões: uma em que cada thread invoca 
um método increment da classe Counter e outra em que as threadas acedem directamente a uma variável de instância. */

class Counter{
	private int x;

	public Counter(){
		this.x = 0
	}
	public void printC(){
		System.out.println(x)
	}
}

class MyThread extends Thread{
	private Counter c;
	private int I;
	this.c = counter
	public MyThread(Counter counter, int i){
		this.c = counter;
		this.I = i;
	}
	public void run(){
		for(int i = 1; i<I; i++){
			this.c++;
			this.c.printC();
		}
	}
}

public class Ex2{
	public static void main(String[] args){
		try{
			final int N = Integer.parseInt(args[0]);
		    final int I = Integer.parseInt(args[1]);
		    Counter counter = new Counter();
		    for(int i = 0; i<N; i++){
			    Thread t = new MyThread(counter, I);
			    t.I = I;
			    t.start();
		}
		counter.printC();
		System.out.println("Finish");
	}
}

//--------------------------Outra Forma Ex2-----------------------------------------//

class Counter{
	final int value;
}

class Incrementer extends Thread{
	final int iterations;
	final Counter c = new Counter();
	Incrementer(int iterations, Counter c){ 
		this.iterations = iterations;
	    this.c = c;
	}
	public void run(){
		for(int i = 0; i<iterations; ++i){
			c.value+=1;
		}
	}
}

class Main{
	public static void main(String[] args) throws InterruptedException{
		final int N = 	Integer.parseInt(args[0]);
		final int I = 	Integer.parseInt(args[1]);
		Counter c = new Counter();
		Thread[] a = new Thread[N];
		for(int i = 0; i<N; ++i){
			a[i] = new Incrementer(I,c);
		}
		for(int i = 0; i<N; ++i){
			a[i].start();
		}
		for(int i = 0; i<N; ++i){
			a[i].join();
		}
		System.out.println(c.value());
	}
}

//-----------------------------Ex2----------------------------------------------//

// Encapsulada

class Counter{
	private int value;
	void increment(){ value +=1;}
	int value(){ return value;}
}

class Incrementer extends Thread{
	final int iterations;
	final Counter c = new Counter();
	Incrementer(int iterations, Counter c){ 
		this.iterations = iterations;
	    this.c = c;
	}
	public void run(){
		for(int i = 0; i<iterations; ++i){
			c.increment();
		}
	}
}

class Main{
	public static void main(String[] args) throws InterruptedException{
		final int N = 	Integer.parseInt(args[0]);
		final int I = 	Integer.parseInt(args[1]);
		Counter c = new Counter();
		Thread[] a = new Thread[N];
		for(int i = 0; i<N; ++i){
			a[i] = new Incrementer(I,c);
		}
		for(int i = 0; i<N; ++i){
			a[i].start();
		}
		for(int i = 0; i<N; ++i){
			a[i].join();
		}
		System.out.println(c.value());
	}
}

/* 3. Fazendo a thread principal escrever o valor do contador depois de as outras threads terem terminado, corra várias vezes 
o programa, para diferentes valores de N e I e observe o resultado produzido, em ambas as versões. */



//--------------------------Exemplos-------------------------------------------//

class MyThread extends Thread{
	public void run(){
	System.out.println("Hello World");
	}
}

class Main{
	public static void main(String[] args) throws InterruptedException{
	Thread t1 = new MyThread();
	Thread t2 = new MyThread();

	t1.start();
	t2.start();
	//Garante a execução da main só será executada no final de t1 e t2
	t1.join();
	t2.join();
	
	System.out.println("Main");
	}
}

//---------------------------------------------------------------------------//

class MyThread extends Thread{
	public void run(){
		try{
			System.out.println("Hello World");
			Thread.sleep(500);
			sleep(500);
			System.out.println("Hello World");
	  } catch (InterruptedException ignored) {
  
	   }
	}
}

class Main{
	public static void main(String[] args) throws InterruptedException{
	Thread t1 = new MyThread();
	Thread t2 = new MyThread();

	t1.start();
	t2.start();
  
	//Garante a execução da main só será executada no final de t1 e t2
	t1.join();
	t2.join();
	
	System.out.println("Main");
	}
}

//---------------------------------------------------------------------------//

class MyThread extends Thread{
	public void run(){
		try{
			System.out.println("Hello World");
			Thread.sleep(500);
			sleep(500);
			System.out.println("Hello World");
	} catch (InterruptedException ignored) {

	  }
	}
}

class MyRunnable implements Runnable{
	public void run(){
		System.out.println("Runnable");
	}
}

class Main{
	public static void main(String[] args) throws InterruptedException{
	Thread t1 = new MyThread();
	Thread t2 = new MyThread();

	t1.start();
	t2.start();
  
	//Garante a execução da main só será executada no final de t1 e t2
	t1.join();
	t2.join();
	
	System.out.println("Main");

	Thread t = new Thread(new MyRunnable());
	t.start();
	}
}
