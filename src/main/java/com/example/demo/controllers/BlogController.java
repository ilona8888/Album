package com.example.demo.controllers;

import com.example.demo.models.Post;
import com.example.demo.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Controller
public class BlogController {

    @Value("${upload.img}")
    protected String uploadImg;

    @Autowired
    PostRepository postRepository;

    @GetMapping("/blog")
    public String blog(Model model){
        Iterable<Post> posts = postRepository.findAll();
        model.addAttribute("posts",posts);
        return "blog";
    }

    @GetMapping("/blog/add")
    public String blogAdd(Model model){
        return "blogAdd";
    }

    @PostMapping("/blog/add")
    public String blogPostAdd(@RequestParam String title, @RequestParam String anons, @RequestParam String full_text, @RequestParam("filename") MultipartFile image, Model model) throws IOException {

        String resultFileName = createUniqueFile(image);

        Post post = new Post(title,anons,full_text,resultFileName);
        postRepository.save(post);
        return "redirect:/blog";
    }

    @GetMapping("/blog/{id}")
    public String showDetails(@PathVariable(value = "id") Long id, Model model){

        if(!postRepository.existsById(id)){
            return "redirect:/blog";
        }
        Optional<Post> post = postRepository.findById(id);
        post.get().setViews(post.get().getViews() + 1);
        postRepository.save(post.get());
        ArrayList<Post> res = new ArrayList<>();
        post.ifPresent(res :: add); // копируем массив из БД в array list
        model.addAttribute("post", res);
        return "blogShowDetails";
    }

    @GetMapping("/blog/{id}/edit")
    public String edit(@PathVariable(value = "id") Long id, Model model){
        if(!postRepository.existsById(id)){
            return "redirect:/blog";
        }
        Optional<Post> post = postRepository.findById(id);
        ArrayList<Post> res = new ArrayList<>();
        post.ifPresent(res :: add); // копируем массив из БД в array list
        model.addAttribute("post", res);
        return "blogEdit";
    }

    @PostMapping("/blog/{id}/edit")
    public String blogPostEdit(@PathVariable(value = "id") Long id,@RequestParam String title,@RequestParam String anons,@RequestParam String full_text,@RequestParam("filename") MultipartFile image, Model model) throws IOException {
        Post post = postRepository.findById(id).get();
        post.setTitle(title);
        post.setAnons(anons);
        post.setFullText(full_text);

        String resultFileName = createUniqueFile(image);

        post.setFilename(resultFileName);
        postRepository.save(post);
        return "redirect:/blog";
    }

    @GetMapping("/blog/{id}/remove")
    public String blogRemove(@PathVariable(value = "id") Long id, Model model){
        postRepository.deleteById(id);
        return "redirect:/blog";
    }

    public String createUniqueFile(MultipartFile image) throws IOException {
        String uuidFile = UUID.randomUUID().toString();
        String resultFileName = uuidFile + "." + image.getOriginalFilename();
        image.transferTo(new File(uploadImg + "/" + resultFileName));
        return resultFileName;
    }
}
