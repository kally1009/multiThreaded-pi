// Kalicia Adams
// a02276756@aggies.usu.edu
// Programming Assingment 3 (Parallel Pi)


import java.util.*;

import static java.lang.String.valueOf;

public class Assign3 {
    static public void main(String[] args){
        int digitNum = 1000;
        int numCores = Runtime.getRuntime().availableProcessors();
        taskQueue queue = new taskQueue();
        queue.shuffleQueue(digitNum);
        long duration = System.currentTimeMillis();
        while(queue.getSize()>0) {
            if(numCores>0) {
                    Thread object
                            = new Thread(new workerThread());
                    object.start();
                    if(Thread.currentThread()!=null){
                        try {
                            object.join();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            };
        long duration2 = System.currentTimeMillis();
        long time = duration2 - duration;
        System.out.println("Total time to calculate pi: "+(time/1000) + " Seconds");
        printEndResult();
        System.exit(0);
    }

    static class taskQueue {
        static List queue = Collections.synchronizedList(new LinkedList<Integer>());

        public void shuffleQueue(int digitNum) {
            ArrayList start = new ArrayList<Integer>();
            for (int i = 1; i <= digitNum; i++) {
                start.add(i);
            }
            Collections.shuffle(start);
            for (int i = 0; i < start.size(); i++) {
                queue.add(start.get(i));
            }
            ;
        }

        ;

        public static int getFromQueue() {
            if (queue.size() != 0) {
                int nPlace = (int) queue.get(0);
                queue.remove(0);
                return nPlace;
            } else {
                return -1;
            }
        }
        public static int getSize() {
            return queue.size();
        };
    };

        static class ResultTable {
            static HashMap<Integer, Integer> resultTable = new HashMap<Integer, Integer>();

            public static void addResult(int i, int num) {
                resultTable.put(i, num);
                if (resultTable.size() % 10 == 0) {
                    updateProgress();
                }
            }

            private static void updateProgress() {
                System.out.flush();
                if (resultTable.size() % 100 == 0){
                    System.out.println(".");
                }else{
                    System.out.print(".");
                }

            }

            static int returnResult(int n) {
                return resultTable.get(n);
            };

            static int resultTableSize() {
                return resultTable.size();
            };

        };

        static class workerThread implements Runnable {
            public void run() {
                if (taskQueue.getSize() != 0) {
                    try {
                        Bpp bpp = new Bpp();
                        int nPlace = taskQueue.getFromQueue();
                        if(nPlace!=-1) {
                            int decimal = bpp.getDecimal(nPlace);
                            ResultTable.addResult(nPlace, decimal);
                        }
                    } catch (Exception e) {
                        System.out.println(e);

                    }
                }


            }

            ;
        }
        public static class Bpp {


            /**
             * Returns the nth digit of pi followed by the next 8 numbers
             *
             * @param n - nth number of pi to return
             * @return returns an integer value containing 8 digits after n
             */
            public int getDecimal(long n) {
                long av, a, vmax, N, num, den, k, kq, kq2, t, v, s, i;
                double sum;

                N = (long) ((n + 20) * Math.log(10) / Math.log(2));

                sum = 0;

                for (a = 3; a <= (2 * N); a = nextPrime(a)) {

                    vmax = (long) (Math.log(2 * N) / Math.log(a));
                    av = 1;
                    for (i = 0; i < vmax; i++)
                        av = av * a;

                    s = 0;
                    num = 1;
                    den = 1;
                    v = 0;
                    kq = 1;
                    kq2 = 1;

                    for (k = 1; k <= N; k++) {

                        t = k;
                        if (kq >= a) {
                            do {
                                t = t / a;
                                v--;
                            } while ((t % a) == 0);
                            kq = 0;
                        }
                        kq++;
                        num = mulMod(num, t, av);

                        t = (2 * k - 1);
                        if (kq2 >= a) {
                            if (kq2 == a) {
                                do {
                                    t = t / a;
                                    v++;
                                } while ((t % a) == 0);
                            }
                            kq2 -= a;
                        }
                        den = mulMod(den, t, av);
                        kq2 += 2;

                        if (v > 0) {
                            t = modInverse(den, av);
                            t = mulMod(t, num, av);
                            t = mulMod(t, k, av);
                            for (i = v; i < vmax; i++)
                                t = mulMod(t, a, av);
                            s += t;
                            if (s >= av)
                                s -= av;
                        }

                    }

                    t = powMod(10, n - 1, av);
                    s = mulMod(s, t, av);
                    sum = (sum + (double) s / (double) av) % 1;
                }
                return (int) (sum * 1e1); // 1e9 is 9 decimal places
            }

            private long mulMod(long a, long b, long m) {
                return (long) (a * b) % m;
            }

            private long modInverse(long a, long n) {
                long i = n, v = 0, d = 1;
                while (a > 0) {
                    long t = i / a, x = a;
                    a = i % x;
                    i = x;
                    x = d;
                    d = v - t * x;
                    v = x;
                }
                v %= n;
                if (v < 0)
                    v = (v + n) % n;
                return v;
            }

            private long powMod(long a, long b, long m) {
                long tempo;
                if (b == 0)
                    tempo = 1;
                else if (b == 1)
                    tempo = a;

                else {
                    long temp = powMod(a, b / 2, m);
                    if (b % 2 == 0)
                        tempo = (temp * temp) % m;
                    else
                        tempo = ((temp * temp) % m) * a % m;
                }
                return tempo;
            }

            private boolean isPrime(long n) {
                if (n == 2 || n == 3)
                    return true;
                if (n % 2 == 0 || n % 3 == 0 || n < 2)
                    return false;

                long sqrt = (long) Math.sqrt(n) + 1;

                for (long i = 6; i <= sqrt; i += 6) {
                    if (n % (i - 1) == 0)
                        return false;
                    else if (n % (i + 1) == 0)
                        return false;
                }
                return true;
            }

            private long nextPrime(long n) {
                if (n < 2)
                    return 2;
                if (n == 9223372036854775783L) {
                    System.err.println("Next prime number exceeds Long.MAX_VALUE: " + Long.MAX_VALUE);
                    return -1;
                }
                for (long i = n + 1; ; i++)
                    if (isPrime(i))
                        return i;
            }

        };

        public static void printEndResult() {
            System.out.println("The final calculation: ");
            String resultString = ("3.");
            for (int i = 1; i < ResultTable.resultTableSize(); i++) {
                int value = ResultTable.returnResult(i);
                resultString= resultString + (valueOf(value));
            }
            System.out.println(resultString);

        };
    };


