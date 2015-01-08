import java.util.List;

import static org.mockito.Mockito.*;

import org.junit.Test;
import static org.junit.Assert.*;

public class MockingTest {
	// Create and train mock
	@Test
	public void test() {
		List<String> mockedList = mock(List.class);
		when(mockedList.get(0)).thenReturn("first");

		// check value
		assertEquals("first", mockedList.get(0));

		// verify interaction
		verify(mockedList).get(0);
	}
}
