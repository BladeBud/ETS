from http.server import HTTPServer, BaseHTTPRequestHandler
import os, sys, traceback, time


tickets_str = open("data.txt","r").read().replace(" ","").split("+")  # load 'tickets' exported from main server formatted as '{"idobjednavka":<int>,"num_seats":<int>},  +\n ...  +\n {"idobjednavka":<int>,"num_seats":<int>}'
tickets_dict = [eval(t.replace("},","}")) for t in tickets_str]
tickets = {t["idobjednavka"]:t["num_seats"] for t in tickets_dict}

#tickets = eval(open("logdata.txt","r").read())  # restore 'tickets' from file after crash/termination containing its last value printed to stderr


class SimpleHTTPRequestHandler(BaseHTTPRequestHandler):

    def do_GET(self):

        print(end="\n\n\n\n",file=sys.stderr)
        print(time.ctime(),file=sys.stderr)
        print(self.headers,file=sys.stderr)
        print(self.path,file=sys.stderr)

        try:
            requested_file = self.path.lstrip('/')

            if requested_file == "app" or requested_file == "":
                self.send_response(200)
                self.send_header('Content-type', 'text/html')
                self.end_headers()

                with open("index.html", 'rb') as f:
                    self.wfile.write(f.read())

            elif "ticket" in requested_file:
                self.send_response(200)
                self.send_header('Content-type', 'text/html')
                self.end_headers()

                id = int(requested_file.split("=")[1])
                if not id in tickets.keys():
                    self.wfile.write(b"FAKE")
                elif tickets[id] > 0:
                    self.wfile.write(b"OK")
                    tickets[id]-=1
                else:
                    self.wfile.write(b"DUP")

                print(tickets,file=sys.stderr)

            elif requested_file == "kill":
                httpd.shutdown()

            else:
                self.send_error(404, "file not found")

        except:
            print(traceback.format_exc(),file=sys.stderr)
            self.send_error(500, "server error")



httpd = HTTPServer(('', 5007), SimpleHTTPRequestHandler)
httpd.serve_forever()
