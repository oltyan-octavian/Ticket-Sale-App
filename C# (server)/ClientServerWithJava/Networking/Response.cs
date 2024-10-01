using System;

namespace Networking
{
    [Serializable]
    public class Response
    {
        public ResponseType Type { get; private set; }
        public object Data { get; private set; }

        private Response() { }

        public class Builder
        {
            private Response response;

            public Builder()
            {
                response = new Response();
            }

            public Builder Type(ResponseType type)
            {
                response.Type = type;
                return this;
            }

            public Builder Data(object data)
            {
                response.Data = data;
                return this;
            }

            public Response Build()
            {
                return response;
            }
        }

        public override string ToString()
        {
            return "Response{" +
                   "Type=" + Type +
                   ", Data=" + Data +
                   '}';
        }
    }
}