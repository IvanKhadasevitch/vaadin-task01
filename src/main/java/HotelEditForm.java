import backend.Hotel;
import backend.HotelCategory;
import backend.HotelService;
import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

public class HotelEditForm extends FormLayout {
    private HotelUI ui;
    private HotelService hotelService = HotelService.getInstance();
    private Hotel hotel;
    private Binder<Hotel> hotelBinder = new Binder<>(Hotel.class);
    private Long id;

    private TextField name = new TextField("Name:");
    private TextField address = new TextField("Address:");
    private TextField rating = new TextField("Rating:");
    private DateField operatesFrom = new DateField("Operates from:");
    private NativeSelect<HotelCategory> category = new NativeSelect<>("Category:");
    private TextField url = new TextField("URL:");
    private TextArea description = new TextArea("Description:");

    private Button saveHotelBtn = new Button("Save");
    private Button closeFormBtn = new Button("Close");

    public HotelEditForm(HotelUI hotelUI) {
        this.ui = hotelUI;

        this.setSizeUndefined();    // Clears any size settings.
        this.setMargin(true);       // Enable layout margins. Affects all four sides of the layout
        this.setVisible(false);

        HorizontalLayout buttons = new HorizontalLayout(saveHotelBtn, closeFormBtn);
        buttons.setSpacing(true);

        this.addComponents(buttons, name, address, rating, operatesFrom, category, url, description);

        // Required fields
        name.setRequiredIndicatorVisible(true);
        hotelBinder.forField(name)
                   // Shorthand for requiring the field to be non-empty
                   .asRequired("Every hotel must have a name")
                   .bind(Hotel::getName, Hotel::setName);

        // connect entity fields with form fields
        hotelBinder.bindInstanceFields(this);

        category.setItems(HotelCategory.values());

        saveHotelBtn.setStyleName(ValoTheme.BUTTON_FRIENDLY);
        saveHotelBtn.addClickListener(e -> saveHotel());
        closeFormBtn.addClickListener(e -> this.setVisible(false));

    }

    public void saveHotel() {
        // This will make all current validation errors visible
        BinderValidationStatus<Hotel> status = hotelBinder.validate();
        if (status.hasErrors()) {
            Notification.show("Validation error count: "
                    + status.getValidationErrors().size());
        }

        // save validated hotel with not empty name
        if ( !status.hasErrors() ) {
            hotelService.save(getHotel());
            ui.updateHotelList();
            this.setVisible(false);
        }
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.setVisible(true);
        this.hotel = hotel;
        // connect entity fields with form fields
        hotelBinder.setBean(hotel);
    }

}
