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
Banco, com saldo inicial nulo. Utilize exclusão mútua ao nível do objecto Banco.

interface Bank{
	void deposit(int id,int val) throws InvalidAccount;
	void witdraw(int id, int val) throws InvalidAccount, NotEnoughFunds;
	int totalBalance(int accounts[]) throws InvalidAccount;
	void transfer(int from, int to, int amount) throws InvalidAccount, NotEnoughFunds; //Exercício 3
} */

public class NotEnoughFunds extends Exception{
	public NotEnoughFundsException(){super();}
	public NotEnoughFundsException(String msg){
		super("Sem Fundos Suficientes "+msg);
	}
}

public class InvalidAccount extends Exception{
	public InvalidAccountException(){super();}
	public InvalidAccountException(String msg){
		super("Conta Invalida "+msg);
	}
}

class Bank{
	private static class Account{
		private int balance;
		public int balance(){
			return balance;
		}
		public void deposit(int val){
			balance += val;
		}
		public void witdraw(int val) throws NotEnoughFunds{
			if (balance < val) throw new NotEnoughFunds();
			balance -= val;
		}
	}
	public Account[] accounts;
	private Account get(int id) throws InvalidAccount{
		if(id < 0 || id >= accounts.length) throw new InvalidAccount();
		return accounts[id];
	}
	public Bank(int n){ 
		accounts = new Account[n];
		for(int i=0; i<accounts.length; ++i) accounts[i] = new Account();
	}
	/* Passar de public synchronized void deposit(int id,int val) throws InvalidAccount{
		if(id<0 || id >= accounts.length) throw new InvalidAccount();
		accounts[id].deposit(val);

		Ou get(id).deposit(val);*/
	public void deposit(int id,int val) throws InvalidAccount{
		Account c = get(id);
		synchronized(this){
			c.deposit(val);
		}
	}
	/* public synchronized void witdraw(int id, int val) throws InvalidAccount, NotEnoughFunds{
		if(id < 0 || id >= accounts.length) throw new InvalidAccount();
		accounts[id].witdraw(val);
		Ou get(id).witdraw(val);*/
	public void witdraw(int id, int val) throws InvalidAccount, NotEnoughFunds{
		Account c = get(id);
		synchronized(this){
			c.witdraw(val);
		}
	}
	public synchronized int totalBalance(int accounts[]) throws InvalidAccount{
		int total = 0;
		for(int i = 0; i < accounts.length; i++){
			if(i < 0 || i >= accounts.length) throw new InvalidAccount();
			total += accounts[i].balance();
		}
		return total;
		/*
		Ou
		int total = 0;
		for(int id : accounts){
			total+=get(id).balance();
			}
			return total;
		}
		*/
	}
}

class Depositer extends Thread{
	final int iterations;
	final Bank b;
	Depositor(int iterations, Bank b){ 
		this.iterations = iterations;
	    this.b = b;
	}
	public void run(){
		for(int i = 0; i<iterations; ++i){
			b.deposit(i % b.accounts.length,1);
		}
	}
}

class Main{
	public static void main(String[] args) throws InterruptedException, InvalidAccount{
		final int N = 	Integer.parseInt(args[0]);
		final int NC = 	Integer.parseInt(args[1]);
		final int I = Integer.parseInt(args[2]);

		Bank b = new Bank(NC);
		Thread[] a = new Thread[N];
		int todasContas = new int[NC];

		for(int i = 0; i<NC; ++i){
			todasContas[i] = i;
		}

		/*
		for(int i = 0; i<NC; ++i){
			b.deposit(i, 1000);
		}
		*/

		for(int i = 0; i<N; ++i){
			a[i] = new Depositor(I,b);
			a[i].start();
			a[i].join();
		}
		System.out.println(b.totalBalance(todasContas));
	}
}

/* 3. Acrescente o método transferir à classe Banco: 

void transfer(int from, int to, int amount) throws InvalidAccount, NotEnoughFunds;

Considere a viabilidade de este ser implementado simplesmente como a composição sequencial das operações de débito e crédito já implementadas. */

public class NotEnoughFunds extends Exception{
	public NotEnoughFundsException(){super();}
	public NotEnoughFundsException(String msg){
		super("Sem Fundos Suficientes "+msg);
	}
}

public class InvalidAccount extends Exception{
	public InvalidAccountException(){super();}
	public InvalidAccountException(String msg){
		super("Conta Invalida "+msg);
	}
}

class Bank{
	private static class Account{
		private int balance;
		public synchronized int balance(){
			return balance;
		}
		public void deposit(int val){
			balance += val;
		}
		public void witdraw(int val) throws NotEnoughFunds{
			if (balance < val) throw new NotEnoughFunds();
			balance -= val;
		}
	}
	public Account[] accounts;
	private Account get(int id) throws InvalidAccount{
		if(id < 0 || id >= accounts.length) throw new InvalidAccount();
		return accounts[id];
	}
	public Bank(int n){ 
		accounts = new Account[n];
		for(int i=0; i<accounts.length; ++i) accounts[i] = new Account();
	}
	public void deposit(int id,int val) throws InvalidAccount{
		Account c = get(id);
		synchronized(c){
			c.deposit(val);
		}
	}
	public void witdraw(int id, int val) throws InvalidAccount, NotEnoughFunds{
		Account c = get(id);
		synchronized(c){
			c.witdraw(val);
		}
	}
	public int totalBalance(int accounts[]) throws InvalidAccount{
		int total = 0;
		for(int id : accounts){
			total += get(id).balance();
		}
		return total;
	}
	public void transfer(int from, int to, int val) throws InvalidAccount, NotEnoughFundsException{
		if(from == to) return;
		Account cfrom = get(from);
		Account cto = get(to);
		if(from < to){
			a1 = cfrom;
			a2 = cto;
		} else{
			a1 = cto;
			a2 = from;
		}
		synchronized(a1){
			synchronized(a2){
				cfrom.witdraw(val);
		        cto.deposit(val);
			}
		}
	}
}

class Depositer extends Thread{
	final int iterations;
	final Bank b;
	Depositor(int iterations, Bank b){ 
		this.iterations = iterations;
	    this.b = b;
	}
	public void run(){
		for(int i = 0; i<iterations; ++i){
			b.deposit(i % b.accounts.length,1);
		}
	}
}

class Main{
	public static void main(String[] args) throws InterruptedException, InvalidAccount{
		final int N = 	Integer.parseInt(args[0]);
		final int NC = 	Integer.parseInt(args[1]);
		final int I = Integer.parseInt(args[2]);

		Bank b = new Bank(NC);
		Thread[] a = new Thread[N];
		int todasContas = new int[NC];

		for(int i = 0; i<NC; ++i){
			todasContas[i] = i;
		}

		/*
		for(int i = 0; i<NC; ++i){
			b.deposit(i, 1000);
		}
		*/

		for(int i = 0; i<N; ++i){
			a[i] = new Depositor(I,b);
			a[i].start();
			a[i].join();
		}
		System.out.println(b.totalBalance(todasContas));
	}
}
