package org.james.hazelcast.controller;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.james.hazelcast.service.DistributedLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HazelcastController {

	//private static final Logger logger = LoggerFactory.getLogger(HazelcastController.class);

	@Autowired
	private DistributedLock distributedLock;

	@RequestMapping("/trylock")
	public String tryLock() {

		Runnable run = new Runnable() {
			@Override
			public void run() {
				Random random = new Random();
				int i = random.nextInt(1);

				distributedLock.lockAndContinue(i);
			}
		};

		ExecutorService pool = Executors.newFixedThreadPool(2);
		for (int i = 0; i < 5; i++) {
			pool.execute(run);
		}

		pool.shutdown();

		return "done";
	}

}
