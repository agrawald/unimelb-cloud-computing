import {Component, OnDestroy, OnInit} from '@angular/core';
import {StompService} from '@stomp/ng2-stompjs';
import {Message} from '@stomp/stompjs';
import {Subscription} from 'rxjs/Subscription';
import {Observable} from "rxjs/Observable";
import {Estimate} from "./domain/estimate";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, OnDestroy {
  public messages: Observable<Message>;
  subscribed: boolean;
  public mq = new Set<string>();
  public count = 0;
  public onNext = (message: Message) => {
    let estimate: Estimate = <Estimate>JSON.parse(message.body);
    // Store message in "historic messages" queue
    this.mq.add(message.body);
    // Count it
    this.count++;

    // Log it to the console
    console.log(message);
  };
  private subscription: Subscription;

  constructor(private stompSvc: StompService) {
  }

  ngOnInit() {
    this.subscribed = false;

    // Store local reference to Observable
    // for use with template ( | async )
    this.subscribe();
  }

  public subscribe() {
    if (this.subscribed) {
      return;
    }

    // Stream of messages
    this.messages = this.stompSvc.subscribe('/queue/sentiment', {
      'durable': false,
      'auto-delete': false,
      'exclusive': false
    });

    // Subscribe a function to be run onNext message
    this.subscription = this.messages.subscribe(this.onNext);

    this.subscribed = true;
  }

  ngOnDestroy() {
    this.unsubscribe();
  }

  public unsubscribe() {
    if (!this.subscribed) {
      return;
    }

    // This will internally unsubscribe from Stomp Broker
    // There are two subscriptions - one created explicitly, the other created in the template by use of 'async'
    this.subscription.unsubscribe();
    this.subscription = null;
    this.messages = null;

    this.subscribed = false;
  }
}
