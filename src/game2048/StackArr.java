package game2048;

public class StackArr {
	
	private int maxSize;
	private int[][][] stackArray;
	private int top;
	
	public StackArr(int size) {
		maxSize = size;
		stackArray = new int[maxSize][GameBoard.ROWS][GameBoard.COLS];
		top = -1;
	}
	
	public void push (int[][] stack) {
		top++;
		if (top >= maxSize - 1) {
			swap();
		}
		stackArray[top] = stack;
	}
	
	public int[][] pop() {
		return stackArray[top--];
	}
	
	public int[][] peek() {
		return stackArray[top];
	}
	
	public boolean isEmpty() {
		return(top == -1);
	}
	
	public boolean isFull() {
		return(top == maxSize - 1);
	}
	
	public void swap() {
		for (int i = 0; i < top; i++) {
			stackArray[i] = stackArray[i + 1];
		}
		top--;
	}
	
}