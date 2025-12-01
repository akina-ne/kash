package team2.nats.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import team2.nats.service.ResultService;

@Controller
public class RankingController {

  private final ResultService resultService;

  public RankingController(ResultService resultService) {
    this.resultService = resultService;
  }

  // /ranking.html にアクセスされたら /ranking にリダイレクトする
  @GetMapping("/ranking.html")
  public String redirectToRanking() {
    return "redirect:/ranking";
  }

  @GetMapping("/ranking")
  public String ranking(ModelMap model) {
    model.addAttribute("rankings", resultService.getRankings());
    return "ranking"; // Thymeleaf template ranking.html
  }
}
