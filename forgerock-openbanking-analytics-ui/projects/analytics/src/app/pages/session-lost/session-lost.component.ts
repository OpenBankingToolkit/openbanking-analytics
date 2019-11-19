import { Component, OnInit } from '@angular/core';

import { ForgerockAuthRedirectOIDCService } from 'ob-ui-libs/oidc';

@Component({
  selector: 'app-session-lost',
  templateUrl: './session-lost.component.html',
  styleUrls: ['./session-lost.component.scss']
})
export class SessionLostComponent implements OnInit {
  constructor(protected apiService: ForgerockAuthRedirectOIDCService) {}

  ngOnInit() {}

  login() {
    this.apiService.getAuthRedirection().subscribe(function(data) {
      window.location.href = data.toString();
    });
  }
}
