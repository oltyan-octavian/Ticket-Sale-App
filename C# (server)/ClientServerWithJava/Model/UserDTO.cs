using System;
using System.Runtime.Serialization;

namespace Model
{
    [Serializable]
    public class UserDTO : ISerializable
    {
        private string name;
        private string password;

        public UserDTO(string name, string password)
        {
            this.name = name;
            this.password = password;
        }

        public string Name => name;

        public string Password => password;

        public override string ToString()
        {
            return $"UserDTO{{name='{name}', password='{password}'}}";
        }

        // Required for deserialization
        public UserDTO(SerializationInfo info, StreamingContext context)
        {
            if (info == null)
                throw new ArgumentNullException(nameof(info));

            name = info.GetString("Name");
            password = info.GetString("Password");
        }

        // Required for serialization
        public void GetObjectData(SerializationInfo info, StreamingContext context)
        {
            if (info == null)
                throw new ArgumentNullException(nameof(info));

            info.AddValue("Name", name);
            info.AddValue("Password", password);
        }
    }
}