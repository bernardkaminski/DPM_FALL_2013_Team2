//to get starting time:

long tStart = System.currentTimeMillis();

//to get ending time:
long tEnd = System.currentTimeMillis();
//the difference is the amount of time passed between start and end
long tDelta = tEnd - tStart;
//convert to seconds if you want 
double elapsedSeconds = tDelta / 1000.0;

