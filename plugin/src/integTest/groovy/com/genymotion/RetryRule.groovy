package com.genymotion

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class RetryRule implements TestRule {

    private int maxRetryCount

    public RetryRule(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount
    }

    public Statement apply(Statement base, Description description) {
        return statement(base, description)
    }

    private Statement statement(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Throwable caughtThrowable = null

                // retry the test the number of time it is asked
                for (int i = 0; i < maxRetryCount; i++) {
                    try {
                        base.evaluate()
                        return
                    } catch (Throwable t) {
                        caughtThrowable = t
                        String retryErrorOutput = description.displayName + ": Run " + (i + 1) + " failed"
                        System.err.println(retryErrorOutput)
                        System.out.println(retryErrorOutput)
                    }
                }
                String failErrorOutput = description.displayName + ": giving up after " + maxRetryCount + " failures"
                System.err.println(failErrorOutput)
                System.out.println(failErrorOutput)
                throw caughtThrowable
            }
        }
    }
}
