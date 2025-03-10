package com.scmanage.controllers;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.scmanage.config.OAuthAuthenticationSuccessHandler;
import com.scmanage.entities.Contact;
import com.scmanage.entities.User;
import com.scmanage.forms.ContactForm;
import com.scmanage.helpers.AppConstants;
import com.scmanage.helpers.Helper;
import com.scmanage.helpers.Message;
import com.scmanage.helpers.MessageType;
import com.scmanage.services.Contactservice;
import com.scmanage.services.ImageService;
import com.scmanage.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {

    Logger logger = LoggerFactory.getLogger(OAuthAuthenticationSuccessHandler.class);

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    @Autowired
    private Contactservice contactService;

    // note
    // a handler -> a handler to actually show the contact form
    // add contact page/view
    @RequestMapping("/add")
    public String addContactView(Model model) {
        ContactForm contactForm = new ContactForm();
        model.addAttribute("contactForm", contactForm);
        contactForm.setFavorite(true);
        return "user/add_contact"; // remember rout hum tagda banayyyy
    }

    // add-new-contact
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String processContactAddForm(@Valid @ModelAttribute ContactForm contactForm, BindingResult bindingResult,
            Authentication authentication, HttpSession session) {

        // how do i check un identified errors int ids in fillowing if block
        // validate form
        if (bindingResult.hasErrors()) {

            // bindingResult.getAllErrors().forEach(error -> logger.info(error.toString()));

            Message message = Message.builder().content("Enter Contact Details Correctly").type(MessageType.red)
                    .build();
            session.setAttribute("message", message);
            return "user/add_contact";
        }

        // How to conver contactform class to contact class
        // User retrieve
        String username = Helper.getEmailOfLoggedInUser(authentication);

        User user = userService.getUserByEmail(username);

        String filename = UUID.randomUUID().toString();

        // Image processing from contact fro
        // logger.info("file Information: {}", contactForm.getContactImage().getOriginalFilename());
        // Uplode karne ka code
        String fileUrl = imageService.uplodeImage(contactForm.getContactImage(), filename);

        Contact contact = new Contact();
        contact.setUser(user);
        contact.setName(contactForm.getName());
        contact.setEmail(contactForm.getEmail());
        contact.setPhoneNumber(contactForm.getPhoneNumber());
        contact.setAddress(contactForm.getAddress());
        contact.setDescription(contactForm.getDescription());
        contact.setFavorite(contactForm.isFavorite());
        contact.setWebsiteLink(contactForm.getWebsiteLink());
        contact.setInstagramLink(contactForm.getInstagramLink());
        contact.setLinkedinLink(contactForm.getLinkedinLink());
        contact.setPicture(fileUrl);
        contact.setCloudinaryImagePublicId(filename);
        // logger
        // DefaultOAuth2User user = (DefaultOAuth2User) authentication.getPrincipal();
        // user.getAttribute()
        System.out.println(contactForm);
        contactService.SavaContacts(contact);

        Message message = Message.builder().content("Contact Added Successfully!").type(MessageType.green).build();
        session.setAttribute("message", message);
        // User

        return "redirect:/user/contacts/add";
    }

    int previous = 0;

    @RequestMapping
    public String getContacts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
            @RequestParam(value = "sort", defaultValue = "name") String sortingField,
            @RequestParam(value = "order", defaultValue = "asc") String sortDirection,
            Authentication authentication,
            Model model) {
        // load all the contacts for the principle
        String username = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(username);
        // List<Contact> contacts = contactService.getByUser(user);
        // int pageNo = this.getContactsPage(0);
        Page<Contact> pageContacts = contactService.getByUser(user, page, size, sortingField, sortDirection);
        
        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);
        
        model.addAttribute("pageContacts", pageContacts);   
        return "user/contacts";
        
    }
    // @RequestMapping("/{page}")
    // public String getContactsPage(@PathVariable int page, Model
    // model,Authentication authentication){
    // String username = Helper.getEmailOfLoggedInUser(authentication);
    // User user = userService.getUserByEmail(username);
    // // List<Contact> contacts = contactService.getByUser(user);
    // // int pageNo = this.getContactsPage(0);
    // Page<Contact> contacts = contactService.getByUser(user,page,10);
    // model.addAttribute("contacts", contacts);
    // return "user/contacts";

    // }

    @RequestMapping("/search")
    public String SearchHandler(
            @RequestParam(value = "field", defaultValue = "") String field,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
            @RequestParam(value = "sort", defaultValue = "name") String sortingField,
            @RequestParam(value = "order", defaultValue = "asc") String sortDirection,
            Model model, Authentication authentication) {
        // logger.info("field {} keyword {}", field, keyword);


        var user = userService.getUserByEmail(Helper.getEmailOfLoggedInUser(authentication));

        Page<Contact> pageContacts = null;
        if (field.equalsIgnoreCase("name")) {
            pageContacts = contactService.searchByName(keyword, page, size, sortingField, sortDirection,user);
        } else if (field.equalsIgnoreCase("email")) {
            pageContacts = contactService.searchByEmail(keyword, page, size, sortingField, sortDirection,user);
        } else if (field.equalsIgnoreCase("phone")) {
            pageContacts = contactService.searchByPhone(keyword, page, size, sortingField, sortDirection,user);
        }
        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);

        model.addAttribute("pageContacts", pageContacts);
        return "user/search";

    }


    @RequestMapping("/delete/{ContactId}")
    public String deleteContact(@PathVariable String ContactId) {
        // logger.info("delete id {}", ContactId);
        contactService.DeleteContact(ContactId);
        return "redirect:/user/contacts";
    }   
    

}
