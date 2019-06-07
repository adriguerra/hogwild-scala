# hogwild-scala

## Abstract

The goal of this project is to design, implement and experiment an asynchronous version in Scala of a distributed stochastic gradient descent (SGD) used in Support Vector Machines (SVMs). Our system exploits the multicore processing capability of modern servers by running the algorithm in multiple threads in parallel and uses unprotected shared memory to concurrently modify a common representation of the weights vector.
The main reference for this project is the [Hogwild! paper](https://people.eecs.berkeley.edu/~brecht/papers/hogwildTR.pdf). The Hogwild! paper is an important paper in the Machine Learning and Parallel Computing community that shows that SGD can be implemented without any locking when the associated optimization problem is sparse. Additionally, our implementation is compared to the asynchronous version of last year's implementation in [Python of the Hogwild! algorithm by EPFL students](https://github.com/liabifano/hogwild-python). This project is part of the [*CS-449 Systems for Data Science*](http://edu.epfl.ch/coursebook/en/systems-for-data-science-CS-449) course taught at EPFL in the Spring semester of 2019.
