package com.siemens.internship;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class InternshipApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // ObjectMapper to convert objects to JSON

    @Test
    void testForItemValid() throws Exception {
        // Create a valid item
        Item validItem = new Item();
        validItem.setName("Item Name");
        validItem.setDescription("Description");
        validItem.setEmail("email@example.com");

        // Convert the Item object to JSON using ObjectMapper
        String itemJson = objectMapper.writeValueAsString(validItem);

        // Test createItem
        mockMvc.perform(MockMvcRequestBuilders.post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Item Name"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.email").value("email@example.com"));
    }

    @Test
    void testForItemInvalid() throws Exception {
        // Create an invalid item
        Item validItem = new Item();
        validItem.setName("Item Name");
        validItem.setDescription("Description");
        validItem.setEmail("email");

        // Convert the Item object to JSON using ObjectMapper
        String itemJson = objectMapper.writeValueAsString(validItem);

        // Test createItem
        mockMvc.perform(MockMvcRequestBuilders.post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemJson))
                .andExpect(status().isBadRequest());
    }
}
