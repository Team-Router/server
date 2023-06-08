package team.router.recycle.controller;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/redis")
public class RedisController {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisController(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("")
    public String setRedisKey(@RequestBody Map<String, String> req) {
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        try {
            vop.set(req.get("key"), req.get("value"));
            return "set message success";
        } catch (Exception e) {
            return "set message fail";
        }
    }

    @GetMapping("/{key}")
    public String getRedisKey(@PathVariable String key) {
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        return vop.get(key);
    }

}
