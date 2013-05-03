package org.jhurliman.hello.backend;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

import org.bridj.Pointer;
import com.nativelibs4java.opencl.JavaCL;
import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLQueue;
import com.nativelibs4java.opencl.CLProgram;
import com.nativelibs4java.opencl.CLKernel;
import com.nativelibs4java.opencl.CLBuffer;
import com.nativelibs4java.opencl.CLEvent;
import com.nativelibs4java.opencl.CLMem;
import com.nativelibs4java.util.IOUtils;

import org.jhurliman.hello.core.IHelloService;

@Service("HelloService")
public class HelloService implements IHelloService {
	protected final Log log = LogFactory.getLog(this.getClass());

	protected CLContext _clContext;
	protected CLQueue _clQueue;
	protected CLKernel _clSquareKernel;

	public HelloService() throws IOException {
		initCL();
	}

	public String getGreeting() {
		float[] numbers = { 0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f };
		float[] squaredNumbers = squareNumbers(numbers);

		String greeting = "Hello World - " + squaredNumbers[0];
		for (int i = 1; i < squaredNumbers.length; i++)
			greeting += "," + squaredNumbers[i];

		return greeting;
	}

	protected void initCL() throws IOException {
		_clContext = JavaCL.createBestContext();
		_clQueue = _clContext.createDefaultQueue();

		String src = IOUtils.readText(HelloService.class.getResourceAsStream("/helloKernels.cl"));
		CLProgram program = _clContext.createProgram(src);
		_clSquareKernel = program.createKernel("square");
	}

	protected float[] squareNumbers(float[] input) {
		// Copy input data to a new allocation of native (host) memory
		Pointer<Float> ptrIn = Pointer.pointerToFloats(input);
		// Allocate native (host) memory for the output data
		Pointer<Float> ptrOut = Pointer.allocateFloats(input.length).order(_clContext.getByteOrder());

		// Create CLBuffer wrappers around input/output buffers, no copying
		CLBuffer<Float> bufIn = _clContext.createBuffer(CLMem.Usage.Input, ptrIn, false);
		CLBuffer<Float> bufOut = _clContext.createBuffer(CLMem.Usage.Output, ptrOut, false);

		// Setup the method arguments for the kernel
		_clSquareKernel.setArg(0, bufIn);
		_clSquareKernel.setArg(1, bufOut);
		_clSquareKernel.setArg(2, input.length);

		// Enqueue execution of the kernel
		CLEvent completion = _clSquareKernel.enqueueNDRange(_clQueue, new int[] { input.length });
		// Wait for the kernel to execute
		completion.waitFor();

		// Map the output CLBuffer so we can safely read from it
		bufOut.map(_clQueue, CLMem.MapFlags.Read);
		// Copy output native (host) memory to a JVM-managed float array
		float[] output = ptrOut.getFloats();
		// Unmap the output CLBuffer now that reading is finished
		bufOut.unmap(_clQueue, ptrOut);

		return output;
	}
}
