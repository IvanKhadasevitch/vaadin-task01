import backend.Hotel;
import backend.HotelService;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import javax.servlet.annotation.WebServlet;
import java.util.List;

@Title("Vaadin task #1")
public class HotelUI extends UI {

    final HotelService hotelService = HotelService.getInstance();

    final TextField filterByName = new TextField();
    final Button clearFilterByNameBtn = new Button(VaadinIcons.CLOSE);
    final TextField filterByAddress = new TextField();
    final Button clearFilterByAddressBtn = new Button(VaadinIcons.CLOSE);
    final Button addHotelBtn = new Button("Add Hotel");
    final Button deleteHotelBtn = new Button("Delete Hotel");
    final Link linkBookingCom = new Link("booking.com", new ExternalResource("http://www.booking.com"));

    final Grid<Hotel> hotelList = new Grid<>(Hotel.class);

    private HotelEditForm hotelEditForm = new HotelEditForm(this);

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        // UI Configuration
        configureComponents();
        buildLayout();
    }

    private void configureComponents() {
        // filterByName field with clear button
        filterByName.setPlaceholder("Filter by name...");
        filterByName.addValueChangeListener(e -> updateHotelList());
        filterByName.setValueChangeMode(ValueChangeMode.LAZY);
        clearFilterByNameBtn.setDescription("Clear the current filter");
        clearFilterByNameBtn.addClickListener(e -> filterByName.clear());

        // filterByAddress field with clear button
        filterByAddress.setPlaceholder("Filter by address...");
        filterByAddress.addValueChangeListener(e -> updateHotelList());
        filterByAddress.setValueChangeMode(ValueChangeMode.LAZY);
        clearFilterByAddressBtn.setDescription("Clear the current filter");
        clearFilterByAddressBtn.addClickListener(e -> filterByAddress.clear());

        // add Hotel Button
        addHotelBtn.addClickListener(e -> hotelEditForm.setHotel(new Hotel()));

        // delete Hotel Button
        deleteHotelBtn.setStyleName(ValoTheme.BUTTON_DANGER);
        deleteHotelBtn.setEnabled(false);
        deleteHotelBtn.addClickListener(e -> {
            Hotel deleteCandidate = hotelList.getSelectedItems().iterator().next();
            hotelService.delete(deleteCandidate);
            deleteHotelBtn.setEnabled(false);
            updateHotelList();
        });

        // Link Visit booking.com
        linkBookingCom.setDescription("Visit booking.com");
        // Open the URL in a new window/tab
        linkBookingCom.setTargetName("_blank");
        // Indicate visually that it opens in a new window/tab
        linkBookingCom.setIcon(new ThemeResource("icons/external-link.png"));
        linkBookingCom.addStyleName("icon-after-caption");

        // Hotel list
        hotelList.setWidth("100%");
        hotelList.setColumnOrder("name", "address", "rating", "operatesFrom", "category", "url", "description");
        hotelList.removeColumn("id");
        hotelList.removeColumn("persisted");
        hotelList.setSelectionMode(Grid.SelectionMode.SINGLE);
        // delete and edit selected Hotel
        hotelList.asSingleSelect().addValueChangeListener(e -> {
            // when Hotel is chosen - can delete or edit
            if (e.getValue() != null) {
                deleteHotelBtn.setEnabled(true);
                hotelEditForm.setHotel(e.getValue());
            } else {
                deleteHotelBtn.setEnabled(false);
                hotelEditForm.setVisible(false);
            }
        });

        this.updateHotelList();
    }

    private void buildLayout() {
        // filters with close button
        CssLayout filteringByName = new CssLayout();
        filteringByName.addComponents(filterByName, clearFilterByNameBtn);
        filteringByName.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

        CssLayout filteringByAddress = new CssLayout();
        filteringByAddress.addComponents(filterByAddress, clearFilterByAddressBtn);
        filteringByAddress.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

        // tools bar
        HorizontalLayout control = new HorizontalLayout(filteringByName, addHotelBtn,
                deleteHotelBtn, linkBookingCom);
        control.setWidth("100%");
        filteringByName.setWidth("100%");
        control.setExpandRatio(filteringByName, 1);

        // tools bar & HotelList
        VerticalLayout left = new VerticalLayout(control, filteringByAddress, hotelList);
        left.setSizeFull();
        hotelList.setSizeFull();
        left.setExpandRatio(hotelList, 1);

        // main layout
        HorizontalLayout mainLayout = new HorizontalLayout(left, hotelEditForm);
        mainLayout.setSizeFull();
        mainLayout.setExpandRatio(left, 1);

        // Split and allow resizing
        setContent(mainLayout);

    }

    public void updateHotelList() {
        List<Hotel> hotelList = hotelService.findAll(filterByName.getValue(), filterByAddress.getValue());
        this.hotelList.setItems(hotelList);
    }

    /*
     * Deployed as a Servlet or Portlet.
     *
     * You can specify additional servlet parameters like the URI and UI class
     * name and turn on production mode when you have finished developing the
     * application.
     */
    @WebServlet(urlPatterns = "/*")
    @VaadinServletConfiguration(ui = HotelUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
