package io.costax.idoit.presentation;

import io.costax.idoit.tasks.boundary.Tasks;
import io.costax.idoit.tasks.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/views/tasks")
public class TaskController {

    @Autowired
    private Tasks tasks;

    @RequestMapping()
    public ModelAndView tasks() {
        List<Task> t = this.tasks.getPendingTasks();
        return new ModelAndView("tasks/tasks", "tasks", t);
    }

    @RequestMapping("{id}")
    public ModelAndView task(@PathVariable("id") Long id, ModelAndView mv) {
        final Optional<Task> t = this.tasks.getById(id);
        return t.map(tk -> new ModelAndView("tasks/task", "task", tk))
                .orElse(new ModelAndView("tasks/error"));
    }


    //@PostMapping
    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public ModelAndView insert(@Valid Task task, BindingResult bindingResult, RedirectAttributes redirectAttributes, ModelAndView mv) {
        if (bindingResult.hasErrors()) {
            return toCreateNew(task);
        }

        tasks.create(task);

        redirectAttributes.addFlashAttribute("sucessMessage", "Task added successful!!!");

        return new ModelAndView("redirect:/views/tasks");
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public ModelAndView delete(@PathVariable("id") Long id, BindingResult bindingResult, RedirectAttributes redirectAttributes, ModelAndView mv) {
        tasks.remove(id);

        return new ModelAndView("redirect:/views/tasks");
    }

    @RequestMapping(value = "/new")
    public ModelAndView toCreateNew(Task task) {

        //logger.debug("going to new Beers");

        // Spring is clever enough to know that if we are putting the Beer parameter in the method declaration
        // we should also want to have it in the template renderization.
        // if no beer instance is present in the invocation then the spring will instantiate a new one.
        // So, we do not need to use the model implicitly to implement this behavior:
        // model.addAttribute(beer));
        // Because Spring will make the invocation of this instruction through behind the scenes

        final ModelAndView modelAndView = new ModelAndView("/tasks/new-task");

        modelAndView.addObject("task", task);

        return modelAndView;
    }
}
