module api.ui.time {

    export class DateTimePicker extends api.dom.DivEl {

        private popup: DateTimePickerPopup;

        private input: api.ui.text.TextInput;

        private popupTrigger: api.ui.button.Button;

        private calendar: Calendar;

        private selectedDate: Date;

        constructor(builder: DateTimePickerBuilder) {
            super('date-time-picker');

            this.input = api.ui.text.TextInput.middle();
            this.input.onClicked((e: MouseEvent) => {
                e.stopPropagation();
                e.preventDefault();

                this.popup.show();
            });

            var wrapper = new api.dom.DivEl('wrapper');
            wrapper.appendChild(this.input);

            this.calendar = new CalendarBuilder().
                setSelectedDate(builder.selectedDate).
                setMonth(builder.month).
                setYear(builder.year).
                setInteractive(true).
                build();

            this.popup = new DateTimePickerPopup(this.calendar, builder);
            wrapper.appendChild(this.popup);

            this.popupTrigger = new api.ui.button.Button();
            this.popupTrigger.addClass('icon-calendar4');
            wrapper.appendChild(this.popupTrigger);

            this.appendChild(wrapper);

            this.popupTrigger.onClicked((e: MouseEvent) => {
                e.stopPropagation();
                e.preventDefault();

                if (this.popup.isVisible()) {
                    this.popup.hide();
                } else {
                    this.popup.show();
                }
            });

            if (builder.selectedDate) {
                this.input.setValue(this.formatDate(builder.selectedDate));
                this.selectedDate = builder.selectedDate;
            }

            if (builder.hours || builder.minutes) {
                var value = this.input.getValue() || "";
                this.input.setValue(value + " " + this.formatTime(builder.hours, builder.minutes));
            }

            this.onSelectedDateChanged((e: SelectedDateChangedEvent) => {
                if (builder.closeOnSelect) {
                    this.popup.hide();
                }
                this.selectedDate = e.getDate();
                this.input.setValue(this.formatDate(e.getDate()) + " " +
                    this.formatTime(this.popup.getSelectedTime().hour, this.popup.getSelectedTime().minute));
            });

            this.onSelectedTimeChanged((hours: number, minutes: number) => {
                if (!this.popup.getSelectedDate()) {
                    this.selectedDate = new Date();
                    this.calendar.selectDate(this.selectedDate);
                }
                this.input.setValue(this.formatDate(this.popup.getSelectedDate()) + " " + this.formatTime(hours, minutes));
            });

            this.input.onKeyUp((event: KeyboardEvent) => {
                if (api.ui.KeyHelper.isNumber(event) ||
                    api.ui.KeyHelper.isDash(event) ||
                    api.ui.KeyHelper.isBackspace(event) ||
                    api.ui.KeyHelper.isDel(event)) {

                    var typedDateTime = this.input.getValue();
                    if (api.util.StringHelper.isEmpty(typedDateTime)) {
                        this.calendar.selectDate(null);
                        this.selectedDate = null;
                        this.popup.hide();
                    } else {
                        var date = api.util.DateHelper.parseUTCDateTime(typedDateTime);
                        var dateLength = date.getUTCFullYear().toString().length + 12;
                        if (date && date.toString() != "Invalid Date" && typedDateTime.length == dateLength) {
                            this.selectedDate = date;
                            this.calendar.selectDate(date);
                            this.popup.setSelectedTime(date.getUTCHours(), date.getUTCMinutes());
                            if (!this.popup.isVisible()) {
                                this.popup.show();
                            }
                        } else {
                            this.selectedDate = null;
                        }
                    }
                }
            });

        }

        getSelectedDateTime(): Date {
            return this.popup.getSelectedDateTime();
        }

        getSelectedDate(): Date {
            return this.selectedDate;
        }

        onSelectedDateChanged(listener: (event: SelectedDateChangedEvent) => void) {
            this.popup.onSelectedDateChanged(listener);
        }

        unSelectedDateChanged(listener: (event: SelectedDateChangedEvent) => void) {
            this.popup.unSelectedDateChanged(listener);
        }

        private formatDate(date: Date): string {
            return api.util.DateHelper.formatUTCDate(date);
        }

        getSelectedTime(): {hour: number; minute: number} {
            return this.popup.getSelectedTime();
        }

        onSelectedTimeChanged(listener: (hours: number, minutes: number) => void) {
            this.popup.onSelectedTimeChanged(listener);
        }

        unSelectedTimeChanged(listener: (hours: number, minutes: number) => void) {
            this.popup.unSelectedTimeChanged(listener);
        }

        formatTime(hours: number, minutes: number): string {
            return this.padNumber(hours, 2) + ':' + this.padNumber(minutes, 2);
        }

        private padNumber(value: number, pad: number): string {
            return Array(pad - String(value).length + 1).join('0') + value;
        }

    }

}