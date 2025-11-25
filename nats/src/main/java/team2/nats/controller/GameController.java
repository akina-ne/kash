package team2.nats.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import team2.nats.repository.ImageRepository;

@Controller
@RequestMapping("/game")
public class GameController {

  @Autowired
  private ImageRepository imageRepository;

  @GetMapping("")
  public String list(ModelMap model) {
    model.addAttribute("images", imageRepository.findAll());
    return "game.html";
  }
}
