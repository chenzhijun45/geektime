

[toc]

### 开启GC日志

首先，需要开启GC日志

**开启参数：** **`-XX:+PrintGC`**  或者  **`-XX:+PrintGCDetails`**，顾名思义，后者会打印具体的详细信息。

如果不想在控制台打印出过多GC，可以把GC打印到指定的文件，开启命令：**`-Xloggc:gc.log`**。

如果想知道GC发生的时间，可以加上打印时间的参数：**`-XX:+PrintGCDateStamps`**

接下里开始具体的GC日志分析。

### 不同垃圾收集器下的GC日志分析

#### GC日志基本介绍

java8默认的垃圾收集器（新生代：Parallel Scavenge  [PS scavenge]；老年代：Parallel Old [PS MarkSweep]）

**一条年轻代GC日志进行分析:**

```java
2021-08-10T14:16:13.864+0800: [GC (Allocation Failure) [PSYoungGen: 76800K->4388K(89600K)] 76800K->4396K(294400K), 0.0114199 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
```

分析：这是一条完整的GC日志信息，可以分为3部分分析 GC时间 + 堆内存变化情况 + cpu使用情况，具体如下

1. 2021-08-10T14:16:13.864+0800：指的是本次GC发生的时间

2. [GC (Allocation Failure) [PSYoungGen: 76800K->4388K(89600K)] 76800K->4396K(294400K), 0.0114199 secs]：指的是本次GC堆内存的变化情况。

   GC (Allocation Failure)：指的是本次GC发生的原因是Allocation Failure)，即内存分配失败；

   [PSYoungGen: 76800K->4388K(89600K)]：指的是年轻代发生GC前后的堆内存使用情况，总计大小89600K，执行GC之前使用量为76800K，执行GC之后使用量是4388K。说明本次GC回收了 76800K-4388K=72412K 的垃圾，使用的垃圾收集器是PS(Parallel Scavenge)垃圾收集器。

   76800K->4396K(294400K), 0.0114199 secs：指的是本次垃圾回收耗时0.0114199秒，整个堆内存大小为294400K，整个堆内存使用量从76800K变为4396K。说明本次GC整个堆回收了 76800K-4396K=72404K的垃圾。

   对比一下可以发现：GC之前，年轻代和整个堆内存的使用大小都是76800K；GC之后，年轻代回收了 72412K 垃圾，整个堆回收了 72404K 垃圾，相差了8K，说明这8K对象晋升到了老年代。

3. [Times: user=0.00 sys=0.00, real=0.01 secs]：指的是本次GC消耗的cpu时间为0.01秒，和GC消耗的时间0.0114199秒差不多。

**一条Full GC日志进行分析：**

```java
2021-08-10T14:16:14.599+0800: [Full GC (Ergonomics) [PSYoungGen: 4321K->0K(96768K)] [ParOldGen: 201504K->130857K(204800K)] 205826K->130857K(301568K), [Metaspace: 3318K->3318K(1056768K)], 0.0208408 secs] [Times: user=0.13 sys=0.00, real=0.02 secs] 
```

分析：Full GC = 年轻代GC + 老年代GC，其日志和年轻代GC大致一样。

PSYoungGen：年轻代GC

ParOldGen：老年代GC

Metaspace：元数据区情况，前后不变

**最后是Heap的情况：**

指的是最后打印出此时的堆内存情况，各个区域的内存使用情况。



#### Serial垃圾收集器GC日志分析

**Serial**是一个**单线程**的新生代垃圾收集器，**串行垃圾收集器**，历史最为悠久，新生代采用“**标记-复制**”算法，老年代(Serial Old)采用“**标记-整理**”算法。执行垃圾收集的时候，会造成**Stop The World**问题。其优点是**简单高效**，是**Client模式**下的默认新生代垃圾收集器。对于几十上百兆的内存管理还是很高效的。

这部分都是-XX:+UseSerialGC ，区别在于堆内存越来越大

##### 启动命令关键参数：**-Xmx512m -Xms512m -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+UseSerialGC** 

GC组合：Copy	
				MarkSweepCompact

```java
2021-08-14T17:46:53.508+0800: [GC (Allocation Failure) 2021-08-14T17:46:53.508+0800: [DefNew: 139776K->17472K(157248K), 0.0221172 secs] 139776K->63945K(506816K), 0.0221687 secs] [Times: user=0.00 sys=0.03, real=0.02 secs] 
省略很多日志......
2021-08-14T17:46:54.416+0800: [Full GC (Allocation Failure) 2021-08-14T17:46:54.417+0800: [Tenured: 349383K->349558K(349568K), 0.0442762 secs] 506345K->404150K(506816K), [Metaspace: 3821K->3821K(1056768K)], 0.0443281 secs] [Times: user=0.05 sys=0.00, real=0.05 secs] 
执行结束：累计生产对象13261次
Heap
 def new generation   total 157248K, used 59243K [0x00000000e0000000, 0x00000000eaaa0000, 0x00000000eaaa0000)
  eden space 139776K,  42% used [0x00000000e0000000, 0x00000000e39dad40, 0x00000000e8880000)
  from space 17472K,   0% used [0x00000000e8880000, 0x00000000e8880000, 0x00000000e9990000)
  to   space 17472K,   0% used [0x00000000e9990000, 0x00000000e9990000, 0x00000000eaaa0000)
 tenured generation   total 349568K, used 349558K [0x00000000eaaa0000, 0x0000000100000000, 0x0000000100000000)
   the space 349568K,  99% used [0x00000000eaaa0000, 0x00000000ffffd920, 0x00000000ffffda00, 0x0000000100000000)
 Metaspace       used 3828K, capacity 4540K, committed 4864K, reserved 1056768K
  class space    used 420K, capacity 428K, committed 512K, reserved 1048576K
```

**分析：** 对于512m的内存，其垃圾回收停顿时间平均在38.5毫秒(来自 gceasy.io分析)，年轻代GC时间较短，20ms左右，Full GC相对较长，40ms左右。



##### 启动命令关键参数：**-Xmx1g -Xms1g-XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+UseSerialGC** 

GC组合：Copy	
				MarkSweepCompact

```java
 
2021-08-14T17:55:13.039+0800: [GC (Allocation Failure) 2021-08-14T17:55:13.039+0800: [DefNew: 279616K->34943K(314560K), 0.0164198 secs] 674191K->516150K(1013632K), 0.0164564 secs] [Times: user=0.02 sys=0.00, real=0.02 secs] 
执行结束：累计生产对象23363次
Heap
 def new generation   total 314560K, used 43609K [0x00000000c0000000, 0x00000000d5550000, 0x00000000d5550000)
  eden space 279616K,   3% used [0x00000000c0000000, 0x00000000c0876670, 0x00000000d1110000)
  from space 34944K,  99% used [0x00000000d3330000, 0x00000000d554ffc8, 0x00000000d5550000)
  to   space 34944K,   0% used [0x00000000d1110000, 0x00000000d1110000, 0x00000000d3330000)
 tenured generation   total 699072K, used 481206K [0x00000000d5550000, 0x0000000100000000, 0x0000000100000000)
   the space 699072K,  68% used [0x00000000d5550000, 0x00000000f2b3db48, 0x00000000f2b3dc00, 0x0000000100000000)
 Metaspace       used 3827K, capacity 4540K, committed 4864K, reserved 1056768K
  class space    used 420K, capacity 428K, committed 512K, reserved 1048576K
```

**分析：**1g的内存，平均耗时35.9ms，虽然没有发生Full GC，但是GC耗时和发生了多次Full GC差不多。主要原因是生成了2w多次对象，导致垃圾回收任务更加繁重。**可见Serial GC不适合大内存，内存越大，产生的对象越多，需要回收的对象越多，耗时会越长。**



#### ParNew垃圾收集器GC日志分析

**PaeNew**是**Serial的多线程版本**垃圾收集器，共用了Serial很多代码，也是一个新生代的垃圾收集器，是**Server模式**下的**首选**新生代垃圾收集器。同样是采用“标记-复制”算法，会造成**Stop The World**问题。因为是多线程并发的执行垃圾收集工作，所以更适合在多CPU环境下工作，默认启动垃圾收集的线程数和CPU数量相等，也可以通过 **-XX:ParallelGCThreads** 参数修改垃圾收集线程数量。**不推荐使用**该垃圾收集器，因为在**不久**的将来**将会被移除**。启用的话，程序会给出警告：`Using the ParNew young collector with the Serial old collector is deprecated and will likely be removed in a future release.`

##### 启动命令关键参数：**-Xmx512m -Xms512m -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+UseParNew**GC

GC组合：ParNew
				MarkSweepCompact

```java
2021-08-14T18:08:32.916+0800: [GC (Allocation Failure) 2021-08-14T18:08:32.916+0800: [ParNew: 139644K->17472K(157248K), 0.0111621 secs] 139644K->55633K(506816K), 0.0118290 secs] [Times: user=0.06 sys=0.00, real=0.01 secs] 
......
2021-08-14T18:08:33.398+0800: [GC (Allocation Failure) 2021-08-14T18:08:33.398+0800: [ParNew: 139720K->139720K(157248K), 0.0000195 secs]2021-08-14T18:08:33.398+0800: [Tenured: 328899K->349214K(349568K), 0.0434176 secs] 468619K->352858K(506816K), [Metaspace: 3319K->3319K(1056768K)], 0.0434911 secs] [Times: user=0.05 sys=0.00, real=0.04 secs] 
2021-08-14T18:08:33.452+0800: [Full GC (Allocation Failure) 2021-08-14T18:08:33.453+0800: [Tenured: 349564K->349442K(349568K), 0.0516625 secs] 506762K->365114K(506816K), [Metaspace: 3319K->3319K(1056768K)], 0.0517155 secs] [Times: user=0.05 sys=0.00, real=0.05 secs] 
......
2021-08-14T18:08:33.837+0800: [Full GC (Allocation Failure) 2021-08-14T18:08:33.837+0800: [Tenured: 349523K->349524K(349568K), 0.0541228 secs] 506662K->382666K(506816K), [Metaspace: 3319K->3319K(1056768K)], 0.0541665 secs] [Times: user=0.06 sys=0.00, real=0.05 secs] 
执行结束：累计生产对象12363次
Heap
 par new generation   total 157248K, used 41434K [0x00000000e0000000, 0x00000000eaaa0000, 0x00000000eaaa0000)
  eden space 139776K,  29% used [0x00000000e0000000, 0x00000000e28768a8, 0x00000000e8880000)
  from space 17472K,   0% used [0x00000000e8880000, 0x00000000e8880000, 0x00000000e9990000)
  to   space 17472K,   0% used [0x00000000e9990000, 0x00000000e9990000, 0x00000000eaaa0000)
 tenured generation   total 349568K, used 349524K [0x00000000eaaa0000, 0x0000000100000000, 0x0000000100000000)
   the space 349568K,  99% used [0x00000000eaaa0000, 0x00000000ffff5310, 0x00000000ffff5400, 0x0000000100000000)
 Metaspace       used 3338K, capacity 4500K, committed 4864K, reserved 1056768K
  class space    used 362K, capacity 388K, committed 512K, reserved 1048576K
```

**分析：**512m内存，和Serial相比，新生代GC耗时较短，如果加上老年代GC耗时的话，就差不多扯平了。平均耗时37.5毫秒。因为ParNew虽然是并行的垃圾回收，但是对于内存不大的情况，线程之间相互切换所消耗的时间也是一笔不小的开销。



##### 启动命令关键参数：**-Xmx1g -Xms1g -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+UseParNew**GC

GC组合：ParNew
				MarkSweepCompact

```java
2021-08-14T18:32:51.289+0800: [GC (Allocation Failure) 2021-08-14T18:32:51.289+0800: [ParNew: 279616K->34943K(314560K), 0.0151709 secs] 279616K->109443K(1013632K), 0.0152214 secs] [Times: user=0.02 sys=0.11, real=0.01 secs] 
......
2021-08-14T18:32:52.118+0800: [GC (Allocation Failure) 2021-08-14T18:32:52.118+0800: [ParNew: 314559K->314559K(314560K), 0.0000259 secs]2021-08-14T18:32:52.118+0800: [Tenured: 610476K->396139K(699072K), 0.0586944 secs] 925036K->396139K(1013632K), [Metaspace: 3836K->3836K(1056768K)], 0.0587722 secs] [Times: user=0.06 sys=0.00, real=0.06 secs] 
2021-08-14T18:32:52.194+0800: [GC (Allocation Failure) 2021-08-14T18:32:52.194+0800: [ParNew: 279616K->34942K(314560K), 0.0158871 secs] 675755K->506641K(1013632K), 0.0159176 secs] [Times: user=0.11 sys=0.00, real=0.02 secs] 
执行结束：累计生产对象23493次
Heap
 par new generation   total 314560K, used 43521K [0x00000000c0000000, 0x00000000d5550000, 0x00000000d5550000)
  eden space 279616K,   3% used [0x00000000c0000000, 0x00000000c0860890, 0x00000000d1110000)
  from space 34944K,  99% used [0x00000000d3330000, 0x00000000d554fb88, 0x00000000d5550000)
  to   space 34944K,   0% used [0x00000000d1110000, 0x00000000d1110000, 0x00000000d3330000)
 tenured generation   total 699072K, used 471698K [0x00000000d5550000, 0x0000000100000000, 0x0000000100000000)
   the space 699072K,  67% used [0x00000000d5550000, 0x00000000f21f4a30, 0x00000000f21f4c00, 0x0000000100000000)
 Metaspace       used 3842K, capacity 4540K, committed 4864K, reserved 1056768K
  class space    used 420K, capacity 428K, committed 512K, reserved 1048576K
```

**分析：**1g内存，产生对象2w多次，平均耗时35.3毫秒，没有发生Full GC。可见内存大点，并没有增加GC消耗的时间，并行垃圾处理的优势开始显现。



#### CMS (Concurrent Mark Sweep 并发-标记-清除)垃圾收集器GC日志分析

**CMS**是**并发**的**老年代**垃圾收集器，并发指的是垃圾收集线程和用户线程可以同时运行。该垃圾收集器**注重**于获得**最短**的**停顿时间**，以**提高**程序的**响应速度**。因为采用的是“**标记-清除**”算法，所以**会产生内存碎片**，当遇到大对象需要分配内存的时候，可能会导致Full GC的发生。一般CMS的GC耗时80%都在remark阶段，也就是最终标记阶段。如果该阶段耗时较长，可尝试添加 `-XX:+CMSScavengeBeforeRemark`，在最终标记之前，先做一次Young GC，目的在于减少年轻代对老年代的无效引用，降低remark时的开销。

##### 启动命令关键参数：**-Xmx512m -Xms512m -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+UseConcMarkSweepGC**

```java
2021-08-14T18:37:29.313+0800: [GC (Allocation Failure) 2021-08-14T18:37:29.313+0800: [ParNew: 139776K->17472K(157248K), 0.0435947 secs] 139776K->59874K(506816K), 0.0436482 secs] [Times: user=0.08 sys=0.01, real=0.04 secs] 
2021-08-14T18:37:29.370+0800: [GC (Allocation Failure) 2021-08-14T18:37:29.370+0800: [ParNew: 157248K->17472K(157248K), 0.0159266 secs] 199650K->118074K(506816K), 0.0159689 secs] [Times: user=0.06 sys=0.00, real=0.02 secs] 
...... 
//初始标记 STW 遍历根节点 耗时很短
2021-08-14T18:37:29.606+0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 311491K(349568K)] 329119K(506816K), 0.0001320 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
//并发标记 耗时最长 由于这个阶段是和用户线程并发的，可能会导致concurrent mode failure
2021-08-14T18:37:29.606+0800: [CMS-concurrent-mark-start]
2021-08-14T18:37:29.608+0800: [CMS-concurrent-mark: 0.002/0.002 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
//并发预清理
2021-08-14T18:37:29.608+0800: [CMS-concurrent-preclean-start]
2021-08-14T18:37:29.609+0800: [CMS-concurrent-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-08-14T18:37:29.609+0800: [CMS-concurrent-abortable-preclean-start]
2021-08-14T18:37:29.609+0800: [CMS-concurrent-abortable-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
//最终标记
2021-08-14T18:37:29.609+0800: [GC (CMS Final Remark) [YG occupancy: 42976 K (157248 K)]2021-08-14T18:37:29.609+0800: [Rescan (parallel) , 0.0004006 secs]2021-08-14T18:37:29.610+0800: [weak refs processing, 0.0000133 secs]2021-08-14T18:37:29.610+0800: [class unloading, 0.0002290 secs]2021-08-14T18:37:29.610+0800: [scrub symbol table, 0.0003479 secs]2021-08-14T18:37:29.610+0800: [scrub string table, 0.0001340 secs][1 CMS-remark: 311491K(349568K)] 354468K(506816K), 0.0011820 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
//并发清除
2021-08-14T18:37:29.610+0800: [CMS-concurrent-sweep-start]
2021-08-14T18:37:29.611+0800: [CMS-concurrent-sweep: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
//并发重置
2021-08-14T18:37:29.611+0800: [CMS-concurrent-reset-start]
2021-08-14T18:37:29.611+0800: [CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-08-14T18:37:29.620+0800: [GC (Allocation Failure) 2021-08-14T18:37:29.620+0800: [ParNew: 157242K->17470K(157248K), 0.0177762 secs] 430047K->349733K(506816K), 0.0178089 secs] [Times: user=0.09 sys=0.02, real=0.02 secs] 
......
2021-08-14T18:37:30.256+0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 349401K(349568K)] 373371K(506816K), 0.0002169 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-08-14T18:37:30.256+0800: [CMS-concurrent-mark-start]
2021-08-14T18:37:30.258+0800: [CMS-concurrent-mark: 0.002/0.002 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-08-14T18:37:30.259+0800: [CMS-concurrent-preclean-start]
执行结束：累计生产对象12681次
Heap
 par new generation   total 157248K, used 57694K [0x00000000e0000000, 0x00000000eaaa0000, 0x00000000eaaa0000)
  eden space 139776K,  41% used [0x00000000e0000000, 0x00000000e3857b58, 0x00000000e8880000)
  from space 17472K,   0% used [0x00000000e9990000, 0x00000000e9990000, 0x00000000eaaa0000)
  to   space 17472K,   0% used [0x00000000e8880000, 0x00000000e8880000, 0x00000000e9990000)
 concurrent mark-sweep generation total 349568K, used 349401K [0x00000000eaaa0000, 0x0000000100000000, 0x0000000100000000)
 Metaspace       used 3825K, capacity 4540K, committed 4864K, reserved 1056768K
  class space    used 420K, capacity 428K, committed 512K, reserved 1048576K
```

**分析：** 512m内存，平均GC耗时18ms，速度大大提升。与其注重点“低停顿高响应”很符合，没有发生Full GC。



##### 启动命令关键参数：**-Xmx1g -Xms1g -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+UseConcMarkSweepGC**

```java
...... 
2021-08-14T18:55:49.010+0800: [GC (Allocation Failure) 2021-08-14T18:55:49.010+0800: [ParNew: 314558K->34942K(314560K), 0.0517577 secs] 821754K->648654K(1013632K), 0.0518011 secs] [Times: user=0.34 sys=0.03, real=0.05 secs] 
2021-08-14T18:55:49.078+0800: [GC (Allocation Failure) 2021-08-14T18:55:49.079+0800: [ParNew: 314558K->314558K(314560K), 0.0000255 secs]2021-08-14T18:55:49.079+0800: [CMS2021-08-14T18:55:49.079+0800: [CMS-concurrent-abortable-preclean: 0.007/0.152 secs] [Times: user=0.59 sys=0.09, real=0.15 secs] 
 (concurrent mode failure): 613712K->367033K(699072K), 0.0497975 secs] 928270K->367033K(1013632K), [Metaspace: 3318K->3318K(1056768K)], 0.0498839 secs] [Times: user=0.05 sys=0.00, real=0.05 secs] 
2021-08-14T18:55:49.145+0800: [GC (Allocation Failure) 2021-08-14T18:55:49.145+0800: [ParNew: 279616K->34943K(314560K), 0.0180457 secs] 646649K->484283K(1013632K), 0.0180852 secs] [Times: user=0.11 sys=0.00, real=0.02 secs] 
2021-08-14T18:55:49.163+0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 449340K(699072K)] 484605K(1013632K), 0.0001141 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-08-14T18:55:49.163+0800: [CMS-concurrent-mark-start]
2021-08-14T18:55:49.167+0800: [CMS-concurrent-mark: 0.003/0.003 secs] [Times: user=0.05 sys=0.00, real=0.00 secs] 
2021-08-14T18:55:49.167+0800: [CMS-concurrent-preclean-start]
2021-08-14T18:55:49.168+0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-08-14T18:55:49.168+0800: [CMS-concurrent-abortable-preclean-start]
2021-08-14T18:55:49.181+0800: [GC (Allocation Failure) 2021-08-14T18:55:49.181+0800: [ParNew: 314559K->34942K(314560K), 0.0219221 secs] 763899K->593752K(1013632K), 0.0219654 secs] [Times: user=0.22 sys=0.00, real=0.02 secs] 
2021-08-14T18:55:49.218+0800: [GC (Allocation Failure) 2021-08-14T18:55:49.218+0800: [ParNew: 314558K->34942K(314560K), 0.0343278 secs] 873368K->695375K(1013632K), 0.0343671 secs] [Times: user=0.19 sys=0.02, real=0.04 secs] 
2021-08-14T18:55:49.255+0800: [CMS-concurrent-abortable-preclean: 0.006/0.087 secs] [Times: user=0.42 sys=0.02, real=0.09 secs] 
2021-08-14T18:55:49.255+0800: [GC (CMS Final Remark) [YG occupancy: 68425 K (314560 K)]2021-08-14T18:55:49.255+0800: [Rescan (parallel) , 0.0004988 secs]2021-08-14T18:55:49.255+0800: [weak refs processing, 0.0000099 secs]2021-08-14T18:55:49.255+0800: [class unloading, 0.0002412 secs]2021-08-14T18:55:49.256+0800: [scrub symbol table, 0.0003304 secs]2021-08-14T18:55:49.256+0800: [scrub string table, 0.0001176 secs][1 CMS-remark: 660432K(699072K)] 728858K(1013632K), 0.0012540 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-08-14T18:55:49.256+0800: [CMS-concurrent-sweep-start]
2021-08-14T18:55:49.257+0800: [CMS-concurrent-sweep: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-08-14T18:55:49.257+0800: [CMS-concurrent-reset-start]
2021-08-14T18:55:49.258+0800: [CMS-concurrent-reset: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-08-14T18:55:49.270+0800: [GC (Allocation Failure) 2021-08-14T18:55:49.270+0800: [ParNew: 314558K->34943K(314560K), 0.0235311 secs] 865732K->692761K(1013632K), 0.0235795 secs] [Times: user=0.17 sys=0.00, real=0.02 secs] 
......
2021-08-14T18:55:49.609+0800: [CMS-concurrent-reset-start]
2021-08-14T18:55:49.610+0800: [CMS-concurrent-reset: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
执行结束：累计生产对象25220次
Heap
 par new generation   total 314560K, used 81569K [0x00000000c0000000, 0x00000000d5550000, 0x00000000d5550000)
  eden space 279616K,  16% used [0x00000000c0000000, 0x00000000c2d887c0, 0x00000000d1110000)
  from space 34944K,  99% used [0x00000000d3330000, 0x00000000d554fda8, 0x00000000d5550000)
  to   space 34944K,   0% used [0x00000000d1110000, 0x00000000d1110000, 0x00000000d3330000)
 concurrent mark-sweep generation total 699072K, used 568832K [0x00000000d5550000, 0x0000000100000000, 0x0000000100000000)
 Metaspace       used 3828K, capacity 4540K, committed 4864K, reserved 1056768K
  class space    used 420K, capacity 428K, committed 512K, reserved 1048576K
```

**分析：** 1g内存，产生对象2.5w次，GC耗时平均19.3ms，没有发生Full GC。可见对于内存增加，对象增加，垃圾增加，总的来说影响不大，较好的发挥了并行的优势。

假如修改代码，让其发生(**concurrent mode failure**)，则**CMS会退化成Serial Old垃圾收集器**，单线程处理。所以导致GC时间大大增加。

为什么会发生concurrent mode failure，是因为年轻代GC后，Eden区和S0的存活对象无法全部放到S1区，所以需要将部分对象放到老年代，但是此时老年代也无法放下这些对象，这个叫**promotion failed**，在此前提下，老年代也恰好在**Full GC**，那么就会出现concurrent mode failure。[参考这里](https://blog.csdn.net/exceptional_derek/article/details/103655604) 

**解决办法：**减少 concurrent mode failure 的出现，所以需要增加老年代或者年轻代的内存大小，或者及时处理内存碎片。及时处理内部才能碎片可通过该参数控制： **-XX:CMSFullGCBeforeCompaction=5**，也就是CMS在进行5次Full GC（标记清除）之后进行一次标记整理算法，控制好老年代的内存碎片，避免出现大对象时无法分配内存。-**XX:+UseCMSCompactAtFullCollection** 意思是对老年代进行压缩，可以消除碎片，但是可能会带来性能消耗。 [参考这里](https://www.jianshu.com/p/ca1b0d4107c5)



#### G1垃圾收集器GC日志分析

##### 启动命令关键参数：**-Xmx512m -Xms512m -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+UseG1GC**

Garbage Firsrt垃圾收集器是一款全能的垃圾收集器，新生代和老年代都能使用，其使命是在未来替换掉CMS垃圾收集器。其工作流程和CMS比较类似。G1可实现高吞吐，没有内存碎片以及收集时间可控，引入了region的概念。

```java
2021-08-14T19:04:43.553+0800: [GC pause (G1 Evacuation Pause) (young), 0.0022287 secs]
   [Parallel Time: 1.7 ms, GC Workers: 8]
      [GC Worker Start (ms): Min: 191.7, Avg: 191.7, Max: 191.7, Diff: 0.0]
      [Ext Root Scanning (ms): Min: 0.1, Avg: 0.3, Max: 1.5, Diff: 1.4, Sum: 2.8]
      [Update RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
         [Processed Buffers: Min: 0, Avg: 0.0, Max: 0, Diff: 0, Sum: 0]
      [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      [Object Copy (ms): Min: 0.0, Avg: 1.1, Max: 1.4, Diff: 1.4, Sum: 9.0]
      [Termination (ms): Min: 0.0, Avg: 0.1, Max: 0.1, Diff: 0.1, Sum: 0.6]
         [Termination Attempts: Min: 1, Avg: 1.0, Max: 1, Diff: 0, Sum: 8]
      [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.0, Sum: 0.3]
      [GC Worker Total (ms): Min: 1.6, Avg: 1.6, Max: 1.6, Diff: 0.0, Sum: 12.7]
      [GC Worker End (ms): Min: 193.3, Avg: 193.3, Max: 193.3, Diff: 0.0]
   [Code Root Fixup: 0.0 ms]
   [Code Root Purge: 0.0 ms]
   [Clear CT: 0.1 ms]
   [Other: 0.5 ms]
      [Choose CSet: 0.0 ms]
      [Ref Proc: 0.2 ms]
      [Ref Enq: 0.0 ms]
      [Redirty Cards: 0.1 ms]
      [Humongous Register: 0.0 ms]
      [Humongous Reclaim: 0.0 ms]
      [Free CSet: 0.0 ms]
   [Eden: 25.0M(25.0M)->0.0B(24.0M) Survivors: 0.0B->4096.0K Heap: 25.0M(512.0M)->8820.5K(512.0M)]
 [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-08-14T19:04:43.559+0800: [GC pause (G1 Evacuation Pause) (young), 0.0027898 secs]
   [Parallel Time: 2.0 ms, GC Workers: 8]
      [GC Worker Start (ms): Min: 197.2, Avg: 197.2, Max: 197.4, Diff: 0.2]
      [Ext Root Scanning (ms): Min: 0.0, Avg: 0.1, Max: 0.2, Diff: 0.2, Sum: 0.9]
      [Update RS (ms): Min: 0.0, Avg: 0.0, Max: 0.2, Diff: 0.2, Sum: 0.3]
         [Processed Buffers: Min: 0, Avg: 0.4, Max: 1, Diff: 1, Sum: 3]
      [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      [Object Copy (ms): Min: 1.5, Avg: 1.7, Max: 1.8, Diff: 0.3, Sum: 13.4]
      [Termination (ms): Min: 0.0, Avg: 0.1, Max: 0.1, Diff: 0.1, Sum: 0.5]
         [Termination Attempts: Min: 1, Avg: 1.1, Max: 2, Diff: 1, Sum: 9]
      [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]
      [GC Worker Total (ms): Min: 1.8, Avg: 1.9, Max: 1.9, Diff: 0.1, Sum: 15.2]
      [GC Worker End (ms): Min: 199.1, Avg: 199.1, Max: 199.2, Diff: 0.0]
   [Code Root Fixup: 0.0 ms]
   [Code Root Purge: 0.0 ms]
   [Clear CT: 0.5 ms]
   [Other: 0.3 ms]
      [Choose CSet: 0.0 ms]
      [Ref Proc: 0.1 ms]
      [Ref Enq: 0.0 ms]
      [Redirty Cards: 0.1 ms]
      [Humongous Register: 0.0 ms]
      [Humongous Reclaim: 0.0 ms]
      [Free CSet: 0.0 ms]
   [Eden: 24.0M(24.0M)->0.0B(34.0M) Survivors: 4096.0K->4096.0K Heap: 32.6M(512.0M)->18.4M(512.0M)]
 [Times: user=0.00 sys=0.00, real=0.00 secs] 
......
2021-08-14T19:04:44.440+0800: [Full GC (Allocation Failure)  460M->368M(512M), 0.0538639 secs]
   [Eden: 0.0B(25.0M)->0.0B(25.0M) Survivors: 0.0B->0.0B Heap: 460.2M(512.0M)->368.7M(512.0M)], [Metaspace: 3624K->3624K(1056768K)]
 [Times: user=0.05 sys=0.00, real=0.05 secs] 
2021-08-14T19:04:44.496+0800: [GC pause (G1 Evacuation Pause) (young), 0.0016014 secs]
   [Parallel Time: 1.2 ms, GC Workers: 8]
      [GC Worker Start (ms): Min: 1135.3, Avg: 1135.4, Max: 1135.6, Diff: 0.3]
      [Ext Root Scanning (ms): Min: 0.0, Avg: 0.1, Max: 0.3, Diff: 0.3, Sum: 1.0]
      [Update RS (ms): Min: 0.0, Avg: 0.1, Max: 0.7, Diff: 0.7, Sum: 0.8]
         [Processed Buffers: Min: 0, Avg: 0.6, Max: 1, Diff: 1, Sum: 5]
      [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      [Object Copy (ms): Min: 0.0, Avg: 0.6, Max: 0.9, Diff: 0.9, Sum: 5.1]
      [Termination (ms): Min: 0.0, Avg: 0.2, Max: 0.8, Diff: 0.8, Sum: 1.8]
         [Termination Attempts: Min: 1, Avg: 4.9, Max: 10, Diff: 9, Sum: 39]
      [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]
      [GC Worker Total (ms): Min: 0.8, Avg: 1.1, Max: 1.2, Diff: 0.3, Sum: 8.8]
      [GC Worker End (ms): Min: 1136.5, Avg: 1136.5, Max: 1136.5, Diff: 0.0]
   [Code Root Fixup: 0.0 ms]
   [Code Root Purge: 0.0 ms]
   [Clear CT: 0.1 ms]
   [Other: 0.3 ms]
      [Choose CSet: 0.0 ms]
      [Ref Proc: 0.1 ms]
      [Ref Enq: 0.0 ms]
      [Redirty Cards: 0.2 ms]
      [Humongous Register: 0.0 ms]
      [Humongous Reclaim: 0.0 ms]
      [Free CSet: 0.0 ms]
   [Eden: 25.0M(25.0M)->0.0B(21.0M) Survivors: 0.0B->4096.0K Heap: 393.7M(512.0M)->378.6M(512.0M)]
 [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-08-14T19:04:44.500+0800: [GC pause (G1 Evacuation Pause) (young) (initial-mark), 0.0018473 secs]
   [Parallel Time: 1.5 ms, GC Workers: 8]
      [GC Worker Start (ms): Min: 1138.9, Avg: 1138.9, Max: 1139.0, Diff: 0.1]
      [Ext Root Scanning (ms): Min: 0.2, Avg: 0.5, Max: 1.4, Diff: 1.3, Sum: 4.1]
      [Update RS (ms): Min: 0.0, Avg: 0.1, Max: 0.1, Diff: 0.1, Sum: 0.5]
         [Processed Buffers: Min: 0, Avg: 1.4, Max: 5, Diff: 5, Sum: 11]
      [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      [Object Copy (ms): Min: 0.0, Avg: 0.8, Max: 1.2, Diff: 1.2, Sum: 6.7]
      [Termination (ms): Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.1, Sum: 0.3]
         [Termination Attempts: Min: 1, Avg: 1.0, Max: 1, Diff: 0, Sum: 8]
      [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      [GC Worker Total (ms): Min: 1.3, Avg: 1.4, Max: 1.5, Diff: 0.1, Sum: 11.6]
      [GC Worker End (ms): Min: 1140.4, Avg: 1140.4, Max: 1140.4, Diff: 0.0]
   [Code Root Fixup: 0.0 ms]
   [Code Root Purge: 0.0 ms]
   [Clear CT: 0.1 ms]
   [Other: 0.2 ms]
      [Choose CSet: 0.0 ms]
      [Ref Proc: 0.1 ms]
      [Ref Enq: 0.0 ms]
      [Redirty Cards: 0.1 ms]
      [Humongous Register: 0.0 ms]
      [Humongous Reclaim: 0.0 ms]
      [Free CSet: 0.0 ms]
   [Eden: 21.0M(21.0M)->0.0B(21.0M) Survivors: 4096.0K->4096.0K Heap: 399.6M(512.0M)->387.0M(512.0M)]
 [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-08-14T19:04:44.502+0800: [GC concurrent-root-region-scan-start]
2021-08-14T19:04:44.502+0800: [GC concurrent-root-region-scan-end, 0.0001915 secs]
2021-08-14T19:04:44.502+0800: [GC concurrent-mark-start]
2021-08-14T19:04:44.505+0800: [GC pause (G1 Evacuation Pause) (young), 0.0019791 secs]
   [Parallel Time: 1.4 ms, GC Workers: 8]
      [GC Worker Start (ms): Min: 1143.2, Avg: 1143.3, Max: 1143.4, Diff: 0.2]
      [Ext Root Scanning (ms): Min: 0.0, Avg: 0.3, Max: 1.3, Diff: 1.3, Sum: 2.1]
      [Update RS (ms): Min: 0.0, Avg: 0.1, Max: 0.1, Diff: 0.1, Sum: 0.5]
         [Processed Buffers: Min: 0, Avg: 1.0, Max: 2, Diff: 2, Sum: 8]
      [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      [Object Copy (ms): Min: 0.0, Avg: 0.9, Max: 1.1, Diff: 1.1, Sum: 7.3]
      [Termination (ms): Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.1, Sum: 0.3]
         [Termination Attempts: Min: 1, Avg: 1.0, Max: 1, Diff: 0, Sum: 8]
      [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      [GC Worker Total (ms): Min: 1.2, Avg: 1.3, Max: 1.3, Diff: 0.2, Sum: 10.4]
      [GC Worker End (ms): Min: 1144.6, Avg: 1144.6, Max: 1144.6, Diff: 0.0]
   [Code Root Fixup: 0.0 ms]
   [Code Root Purge: 0.0 ms]
   [Clear CT: 0.2 ms]
   [Other: 0.3 ms]
      [Choose CSet: 0.0 ms]
      [Ref Proc: 0.1 ms]
      [Ref Enq: 0.0 ms]
      [Redirty Cards: 0.1 ms]
      [Humongous Register: 0.0 ms]
      [Humongous Reclaim: 0.0 ms]
      [Free CSet: 0.0 ms]
   [Eden: 21.0M(21.0M)->0.0B(21.0M) Survivors: 4096.0K->4096.0K Heap: 408.0M(512.0M)->395.1M(512.0M)]
 [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-08-14T19:04:44.507+0800: [GC concurrent-mark-end, 0.0050907 secs]
2021-08-14T19:04:44.507+0800: [GC remark 2021-08-14T19:04:44.507+0800: [Finalize Marking, 0.0001894 secs] 2021-08-14T19:04:44.508+0800: [GC ref-proc, 0.0002214 secs] 2021-08-14T19:04:44.508+0800: [Unloading, 0.0006028 secs], 0.0020721 secs]
 [Times: user=0.01 sys=0.00, real=0.00 secs] 
2021-08-14T19:04:44.510+0800: [GC cleanup 406M->406M(512M), 0.0008409 secs]
 [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-08-14T19:04:44.510+0800: [GC concurrent-cleanup-start]
2021-08-14T19:04:44.511+0800: [GC concurrent-cleanup-end, 0.0000176 secs]
2021-08-14T19:04:44.512+0800: [GC pause (G1 Evacuation Pause) (young), 0.0026994 secs]
   [Parallel Time: 1.9 ms, GC Workers: 8]
      [GC Worker Start (ms): Min: 1150.9, Avg: 1151.0, Max: 1151.5, Diff: 0.6]
      [Ext Root Scanning (ms): Min: 0.0, Avg: 0.3, Max: 1.7, Diff: 1.7, Sum: 2.4]
      [Update RS (ms): Min: 0.0, Avg: 0.1, Max: 0.1, Diff: 0.1, Sum: 0.5]
         [Processed Buffers: Min: 0, Avg: 1.1, Max: 2, Diff: 2, Sum: 9]
      [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      [Object Copy (ms): Min: 0.1, Avg: 1.1, Max: 1.3, Diff: 1.2, Sum: 8.5]
      [Termination (ms): Min: 0.0, Avg: 0.2, Max: 0.3, Diff: 0.3, Sum: 1.9]
         [Termination Attempts: Min: 1, Avg: 8.4, Max: 15, Diff: 14, Sum: 67]
      [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]
      [GC Worker Total (ms): Min: 1.1, Avg: 1.7, Max: 1.8, Diff: 0.6, Sum: 13.4]
      [GC Worker End (ms): Min: 1152.7, Avg: 1152.7, Max: 1152.7, Diff: 0.0]
   [Code Root Fixup: 0.0 ms]
   [Code Root Purge: 0.0 ms]
   [Clear CT: 0.5 ms]
   [Other: 0.3 ms]
      [Choose CSet: 0.0 ms]
      [Ref Proc: 0.1 ms]
      [Ref Enq: 0.0 ms]
      [Redirty Cards: 0.1 ms]
      [Humongous Register: 0.0 ms]
      [Humongous Reclaim: 0.0 ms]
      [Free CSet: 0.0 ms]
   [Eden: 21.0M(21.0M)->0.0B(21.0M) Survivors: 4096.0K->4096.0K Heap: 416.0M(512.0M)->406.4M(512.0M)]
 [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-08-14T19:04:44.517+0800: [GC pause (G1 Evacuation Pause) (mixed) (to-space exhausted), 0.0037592 secs]
   [Parallel Time: 3.2 ms, GC Workers: 8]
      [GC Worker Start (ms): Min: 1155.9, Avg: 1156.2, Max: 1157.9, Diff: 2.1]
      [Ext Root Scanning (ms): Min: 0.0, Avg: 0.1, Max: 0.2, Diff: 0.2, Sum: 0.7]
      [Update RS (ms): Min: 0.0, Avg: 0.2, Max: 0.7, Diff: 0.7, Sum: 1.3]
         [Processed Buffers: Min: 0, Avg: 1.4, Max: 3, Diff: 3, Sum: 11]
      [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]
      [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      [Object Copy (ms): Min: 1.0, Avg: 2.5, Max: 2.8, Diff: 1.8, Sum: 19.8]
      [Termination (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.3]
         [Termination Attempts: Min: 1, Avg: 4.6, Max: 10, Diff: 9, Sum: 37]
      [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      [GC Worker Total (ms): Min: 1.0, Avg: 2.8, Max: 3.1, Diff: 2.1, Sum: 22.2]
      [GC Worker End (ms): Min: 1159.0, Avg: 1159.0, Max: 1159.0, Diff: 0.0]
   [Code Root Fixup: 0.0 ms]
   [Code Root Purge: 0.0 ms]
   [Clear CT: 0.1 ms]
   [Other: 0.5 ms]
      [Evacuation Failure: 0.2 ms]
      [Choose CSet: 0.0 ms]
      [Ref Proc: 0.1 ms]
      [Ref Enq: 0.0 ms]
      [Redirty Cards: 0.1 ms]
      [Humongous Register: 0.0 ms]
      [Humongous Reclaim: 0.0 ms]
      [Free CSet: 0.0 ms]
   [Eden: 21.0M(21.0M)->0.0B(21.0M) Survivors: 4096.0K->4096.0K Heap: 427.4M(512.0M)->438.8M(512.0M)]
 [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-08-14T19:04:44.523+0800: [GC pause (G1 Evacuation Pause) (mixed) (to-space exhausted), 0.0014684 secs]
   [Parallel Time: 0.9 ms, GC Workers: 8]
      [GC Worker Start (ms): Min: 1161.6, Avg: 1161.7, Max: 1161.8, Diff: 0.2]
      [Ext Root Scanning (ms): Min: 0.0, Avg: 0.1, Max: 0.2, Diff: 0.2, Sum: 0.9]
      [Update RS (ms): Min: 0.0, Avg: 0.1, Max: 0.4, Diff: 0.4, Sum: 1.0]
         [Processed Buffers: Min: 0, Avg: 2.8, Max: 7, Diff: 7, Sum: 22]
      [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.1]
      [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      [Object Copy (ms): Min: 0.3, Avg: 0.5, Max: 0.7, Diff: 0.4, Sum: 4.3]
      [Termination (ms): Min: 0.0, Avg: 0.0, Max: 0.1, Diff: 0.1, Sum: 0.3]
         [Termination Attempts: Min: 1, Avg: 1.0, Max: 1, Diff: 0, Sum: 8]
      [GC Worker Other (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      [GC Worker Total (ms): Min: 0.7, Avg: 0.8, Max: 0.9, Diff: 0.2, Sum: 6.5]
      [GC Worker End (ms): Min: 1162.5, Avg: 1162.5, Max: 1162.5, Diff: 0.0]
   [Code Root Fixup: 0.0 ms]
   [Code Root Purge: 0.0 ms]
   [Clear CT: 0.1 ms]
   [Other: 0.5 ms]
      [Evacuation Failure: 0.1 ms]
      [Choose CSet: 0.0 ms]
      [Ref Proc: 0.1 ms]
      [Ref Enq: 0.0 ms]
      [Redirty Cards: 0.1 ms]
      [Humongous Register: 0.0 ms]
      [Humongous Reclaim: 0.0 ms]
      [Free CSet: 0.0 ms]
   [Eden: 19.0M(21.0M)->0.0B(25.0M) Survivors: 4096.0K->0.0B Heap: 457.8M(512.0M)->457.8M(512.0M)]
 [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-08-14T19:04:44.524+0800: [Full GC (Allocation Failure)  457M->371M(512M), 0.0500069 secs]
   [Eden: 0.0B(25.0M)->0.0B(25.0M) Survivors: 0.0B->0.0B Heap: 457.8M(512.0M)->371.9M(512.0M)], [Metaspace: 3836K->3836K(1056768K)]
 [Times: user=0.06 sys=0.00, real=0.05 secs] 
执行结束：累计生产对象12838次
Heap
 garbage-first heap   total 524288K, used 380806K [0x00000000e0000000, 0x00000000e0101000, 0x0000000100000000)
  region size 1024K, 1 young (1024K), 0 survivors (0K)
 Metaspace       used 3843K, capacity 4540K, committed 4864K, reserved 1056768K
  class space    used 420K, capacity 428K, committed 512K, reserved 1048576K

Process finished with exit code 0
```

**分析：**平均GC耗时3.94ms，可见其性能很强，低停顿，目标是取代CMS，没有内存碎片。1g内存的情况差不读，产生了更多的对象，2.5w次，GC平均耗时7ms。引入了region，避免扫描整个堆，所以不会出现堆内存越大GC线性增加的情况。

