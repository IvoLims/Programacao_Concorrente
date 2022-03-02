/* 1. Modifique o exercício do guião anterior de incremento concorrente de um contador partilhado, de modo
a garantir a execução correcta do programa.  */

class Counter{
	private int value;
	synchronized void increment(){ value +=1;}
	// Deveria colocar synchronized int value() { return value;} ? Sim
	synchronized int value(){ return value;}
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

// Exemplo de granoladida temporal aquilo que nao queremos ter exclusão mútua durante muito tempo

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
		synchronized (c){
			for(int i = 0; i<iterations; ++i){
				c.increment();
			}
		}
	}
}

// Controlo de concorrência sobre um objeto, quem tente usar um método sobre o c tem de esperar

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
			synchronized(c){
				c.increment();
			}
		}
	}
}

/* 2. Implemente uma classe Banco que ofereça os métodos da interface abaixo, para crédito, débito e consulta
do saldo total de um conjunto de contas. Considere um número fixo de contas, definido no construtor do
Banco, com saldo inicial nulo. Utilize exclusão mútua ao nível do objecto Banco.*/

interface Bank{
	void deposit(int id,int val) throws InvalidAccount;
	void witdraw(int id, int val) throws InvalidAccount, NotEnoughFunds;
	int totalBalance(int accounts[]) throws InvalidAccount;
	void transfer(int accounts[]) throws InvalidAccount, NotEnoughFunds;
}

public static void main(String[] args) {
	
}
