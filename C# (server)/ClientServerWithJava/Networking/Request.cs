using System;

namespace Networking
{
    [Serializable]
    public class Request
    {
        public RequestType Type { get; private set; }
        public object Data { get; private set; }

        private Request() { }

        public class Builder
        {
            private Request request;

            public Builder()
            {
                request = new Request();
            }

            public Builder Type(RequestType type)
            {
                request.Type = type;
                return this;
            }

            public Builder Data(object data)
            {
                request.Data = data;
                return this;
            }

            public Request Build()
            {
                return request;
            }
        }

        public override string ToString()
        {
            return "Request{" +
                   "Type=" + Type +
                   ", Data=" + Data +
                   '}';
        }
    }
}