function factorial(n) {
    if (n <= 1) {
        return 1;
    }
    return n * factorial(n - 1);
}

function fibonacci(n) {
    if (n <= 0) {
        return 0;
    }
    if (n == 1 || n == 2) {
        return 1;
    }
    return fibonacci(n - 1) + fibonacci(n - 2);
}

function isPrime(num) {
    if (num <= 1) {
        return false;
    }
    
    if (num <= 3) {
        return true;
    }
    
    if (num % 2 == 0 || num % 3 == 0) {
        return false;
    }
    
    i = 5;
    while (i * i <= num) {
        if (num % i == 0 || num % (i + 2) == 0) {
            return false;
        }
        i = i + 6;
    }
    
    return true;
}

function printResults(n) {
    result = "Number: " + n + "\n";
    result = result + "Factorial: " + factorial(n) + "\n";
    result = result + "Fibonacci: " + fibonacci(n) + "\n";
    
    if (isPrime(n)) {
        result = result + n + " is prime";
    } else {
        result = result + n + " is not prime";
    }
    
    return result;
}

// Main program using a for loop
for (i = 1; i <= 10; i = i + 1) {
    print(printResults(i));
    print("---------------");
}