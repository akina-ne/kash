package team2.nats.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import team2.nats.repository.ImageRepository;

@Controller
@RequestMapping("/game")
public class GameController {

  @Autowired
  private ImageRepository imageRepository;

  @GetMapping("")
  public String list(ModelMap model) {
    model.addAttribute("images", imageRepository.findAll());
    return "game"; // templates/game.html
  }

  @PostMapping("/answer")
  public String answer(@RequestParam("answer") String answer, ModelMap model) {
    model.addAttribute("images", imageRepository.findAll());
    model.addAttribute("message", "回答を受け付けました: " + answer);
    return "game";
  }
}
