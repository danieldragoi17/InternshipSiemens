package com.siemens.internship;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
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

    @Test
    void testProcessItems() throws Exception {
        // Create items
        Item item1 = new Item();
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setEmail("email1@example.com");

        String itemJson = objectMapper.writeValueAsString(item1);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemJson))
                .andExpect(status().isCreated());

        // Create items
        Item item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setEmail("email2@example.com");

        itemJson = objectMapper.writeValueAsString(item2);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemJson))
                .andExpect(status().isCreated());

        // Create items
        Item item3 = new Item();
        item3.setName("Item 3");
        item3.setDescription("Description 3");
        item3.setEmail("email3@example.com");

        itemJson = objectMapper.writeValueAsString(item3);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemJson))
                .andExpect(status().isCreated());

        // test process items
        // the items are processed in a random way
        mockMvc.perform(MockMvcRequestBuilders.get("/api/items/process"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Item 1", "Item 2", "Item 3")))
                .andExpect(jsonPath("$[*].description", containsInAnyOrder("Description 1", "Description 2", "Description 3")))
                .andExpect(jsonPath("$[*].status", containsInAnyOrder("PROCESSED", "PROCESSED", "PROCESSED")))
                .andExpect(jsonPath("$[*].email", containsInAnyOrder("email1@example.com", "email2@example.com", "email3@example.com")));
    }
}
