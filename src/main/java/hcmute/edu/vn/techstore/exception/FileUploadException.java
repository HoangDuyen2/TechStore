package hcmute.edu.vn.techstore.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@ControllerAdvice
public class FileUploadException {

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public RedirectView handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex, RedirectAttributes redirectAttributes) {
    redirectAttributes.addFlashAttribute("image", "File upload quá lớn! (tối đa 5MB)");
    return new RedirectView("/register");
  }
}
