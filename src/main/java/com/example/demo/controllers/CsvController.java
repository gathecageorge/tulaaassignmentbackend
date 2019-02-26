package com.example.demo.controllers;

import com.example.demo.models.User;
import com.example.demo.repository.UserRepository;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CsvController {
    @Autowired
    UserRepository userRepository;

    @PostMapping(value = "csv")
    public  Map<String, Object> actionProcessCsv(@RequestParam("file") MultipartFile file,
                                RedirectAttributes redirectAttributes){
        Map<String, Object> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("error", true);
            response.put("message", "Please select a file to upload");
        }else {
            try {
                response.put("error", false);
                response.put("message", "You successfully uploaded '" + file.getOriginalFilename() + "'");

                InputStream inputStream = new ByteArrayInputStream(file.getBytes());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                List<User> users = beanBuilder(reader);
                int index = 1;
                for (User user: users) {
                    if(user.isValid()){
                        try{
                            userRepository.save(user);
                            response.put("line" + index, user.getNational_id() + ": Saved Successfully");
                        }catch (Exception e){
                            response.put("line" + index, user.getNational_id() + ": National ID exists or DB error");
                        }
                    }else{
                        response.put("line" + index, user.getNational_id() + ": " + user.getErrors());
                    }
                    index++;
                }

                //Close the input stream & reader
                inputStream.close();
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
                response.put("error", true);
                response.put("message", "ERROR!!! " + e.getMessage());
            }
        }

        return response;
    }

    public List<User> beanBuilder(BufferedReader bufferedReader) throws Exception {
        CsvToBean<User> csvToBean = new CsvToBean<User>();

        Map<String, String> columnMapping = new HashMap<String, String>();
        columnMapping.put("first_name", "first_name");
        columnMapping.put("last_name", "last_name");
        columnMapping.put("date_of_birth", "date_of_birth");
        columnMapping.put("postal_address", "postal_address");
        columnMapping.put("national_id", "national_id");
        columnMapping.put("gender", "gender");

        HeaderColumnNameTranslateMappingStrategy<User> strategy = new HeaderColumnNameTranslateMappingStrategy<User>();
        strategy.setType(User.class);
        strategy.setColumnMapping(columnMapping);

        CSVReader reader = new CSVReader(bufferedReader);
        return csvToBean.parse(strategy, reader);
    }
}
