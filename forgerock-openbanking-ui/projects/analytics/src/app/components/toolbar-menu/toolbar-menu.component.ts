import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
  // tslint:disable-next-line
  selector: 'analytics-toolbar-menu',
  templateUrl: './toolbar-menu.component.html',
  styleUrls: ['./toolbar-menu.component.scss']
})
export class AnalyticsToolbarMenuComponent implements OnInit {
  @Input() connected: boolean;
  @Input() username: string;
  @Output() logout = new EventEmitter<Event>();

  constructor() {}

  ngOnInit(): void {}

  onLogout(e: Event) {
    this.logout.emit(e);
  }
}
