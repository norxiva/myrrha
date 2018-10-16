package my.norxiva.springboot.sample.codis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class CodisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetSet() throws Exception {
        String key = "foe:my:test:key2";
        String value = "hello3";

        mockMvc.perform(post("/codis/getSet")
                .param("key", key)
                .param("value", value))
                .andExpect(status().isOk());
    }
}
